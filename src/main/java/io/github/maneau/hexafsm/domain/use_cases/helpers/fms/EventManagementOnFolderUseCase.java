package io.github.maneau.hexafsm.domain.use_cases.helpers.fms;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.exceptions.TechException;
import io.github.maneau.hexafsm.domain.use_cases.folder.ChangeFolderStateUseCase;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.EventTypeEnum;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.LinkEnum;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.StateEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventManagementOnFolderUseCase {

    final ChangeFolderStateUseCase changeFolderStateUseCase = ChangeFolderStateUseCase.getInstance();

    @Getter(lazy = true)
    private static final EventManagementOnFolderUseCase instance = new EventManagementOnFolderUseCase();

    public void execute(@NonNull Folder dos, @NonNull EventTypeEnum evt) {
        List<LinkEnum> links = Arrays.stream(LinkEnum.values())
                .filter(l -> l.getEvent() == evt)
                .filter(l -> l.getFrom() == dos.getState())
                .toList();
        if (links.isEmpty()) {
            log.warn("Folder '{}' receive an event '{}' but it does exist link on state {}",
                    dos.getName(), evt.name(), dos.getState().getName());
        } else if (links.size() > 1) {
            log.warn("Folder '{}' receive an event '{}' but it does exist multiple link on state {}",
                    dos.getName(), evt.name(), dos.getState().getName());
        } else {
            LinkEnum link = links.getFirst();
            log.info("Folder '{}' [{}->{}] via event '{}'",
                    dos.getName(), dos.getState().getName(), link.getTo().getName(), evt.name());

            executeChangeState(dos, link.getFrom(), link.getTo(), link.getFailed());
        }
    }

    private void executeChangeState(@NonNull Folder dos, StateEnum fromState, StateEnum toState, StateEnum errState) {
        try {
            //Execute after current State
            fromState.getAfterFunc().forEach(f -> f.execute(dos));

            //Execute before next State
            toState.getBeforeFunc().forEach(f -> f.execute(dos));

            changeFolderStateUseCase.execute(dos, toState);
        } catch (TechException e) {
            log.warn("function before or after failed so dos {} goes to the failed state : {}",
                    dos.getName(), e.getMessage());

            //If Error goes to failed State
            changeFolderStateUseCase.execute(dos, errState);
            log.info("folder '{}' [{}->{}] via failed {}",
                    dos.getName(), dos.getState().getName(), errState.getName(), e.getMessage());

            errState.getBeforeFunc().forEach(f -> f.execute(dos));
        }
    }

}
