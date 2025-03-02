package io.github.maneau.hexafsm.domain.use_cases.helpers.fms;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.entities.FolderLock;
import io.github.maneau.hexafsm.domain.exceptions.TechException;
import io.github.maneau.hexafsm.domain.use_cases.folder.ChangeFolderStateUseCase;
import io.github.maneau.hexafsm.domain.use_cases.folder.TryToLockTheFolderUseCase;
import io.github.maneau.hexafsm.domain.use_cases.folder.UnlockTheFolderUseCase;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.EventTypeEnum;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.LinkEnum;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.StateEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventManagementOnFolderUseCase {

    final ChangeFolderStateUseCase changeFolderStateUseCase = ChangeFolderStateUseCase.getInstance();
    final TryToLockTheFolderUseCase tryToLockTheFolderUseCase = TryToLockTheFolderUseCase.getInstance();
    final UnlockTheFolderUseCase unlockTheFolderUseCase = UnlockTheFolderUseCase.getInstance();

    @Getter(lazy = true)
    private static final EventManagementOnFolderUseCase instance = new EventManagementOnFolderUseCase();

    public void execute(@NonNull Folder folder, @NonNull EventTypeEnum evt) {
        List<LinkEnum> links = Arrays.stream(LinkEnum.values())
                .filter(l -> l.getEvent() == evt)
                .filter(l -> l.getFrom() == folder.getState())
                .toList();
        if (links.isEmpty()) {
            log.info("Folder '{}' receive an event '{}' but it does exist link on state {}",
                    folder.getName(), evt.name(), folder.getState().getName());
        } else if (links.size() > 1) {
            log.warn("Folder '{}' receive an event '{}' but it does exist multiple link on state {}",
                    folder.getName(), evt.name(), folder.getState().getName());
        } else {
            LinkEnum link = links.getFirst();
            log.info("Folder '{}' [{}->{}] via event '{}'",
                    folder.getName(), folder.getState().getName(), link.getTo().getName(), evt.name());

            executeChangeState(folder, link.getFrom(), link.getTo(), link.getFailed());
        }
    }

    private void executeChangeState(@NonNull Folder folder, StateEnum fromState, StateEnum toState, StateEnum errState) {
        //Pessimist lock: we control the lock
        Optional<FolderLock> folderLock = tryToLockTheFolderUseCase.execute(folder);
        if(folderLock.isEmpty()) {
            log.warn("folder '{}' is already locked : ignore the event", folder.getName());
        } else {
            try {
                final LocalDateTime startTime = folder.getUpdateTime();
                //Execute after current State
                fromState.getAfterFunc().forEach(f -> f.execute(folder));

                //Execute before next State
                toState.getBeforeFunc().forEach(f -> f.execute(folder));

                final LocalDateTime endTime = folder.getUpdateTime();
                //Optimise lock control
                if (startTime == endTime) {
                    changeFolderStateUseCase.execute(folder, toState);
                } else {
                    log.error("Folder '{}' has change between the start and the and of transition", folder.getName());
                }
            } catch (TechException e) {
                log.warn("function before or after failed so folder {} goes to the failed state : {}",
                        folder.getName(), e.getMessage());

                //If Error goes to failed State
                changeFolderStateUseCase.execute(folder, errState);
                log.info("folder '{}' [{}->{}] via failed {}",
                        folder.getName(), folder.getState().getName(), errState.getName(), e.getMessage());

                errState.getBeforeFunc().forEach(f -> f.execute(folder));
            } finally {
                unlockTheFolderUseCase.execute(folderLock.get());
            }
        }
    }

}
