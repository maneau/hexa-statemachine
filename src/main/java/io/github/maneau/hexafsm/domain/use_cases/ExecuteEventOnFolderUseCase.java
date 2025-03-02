package io.github.maneau.hexafsm.domain.use_cases;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.exceptions.TechException;
import io.github.maneau.hexafsm.domain.use_cases.folder.GetFolderUseCase;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.EventManagementOnFolderUseCase;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.EventEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExecuteEventOnFolderUseCase {

    @Getter(lazy = true)
    private static final ExecuteEventOnFolderUseCase instance = new ExecuteEventOnFolderUseCase();

    final GetFolderUseCase getFolderUseCase = GetFolderUseCase.getInstance();
    final EventManagementOnFolderUseCase eventManagementOnFolderUseCase = EventManagementOnFolderUseCase.getInstance();

    public void execute(@NonNull UUID dossierId, @NonNull EventEnum eventEnum) {
        Folder folder = getFolderUseCase.execute(dossierId)
                .orElseThrow(() -> new TechException("Folder not found"));
        eventManagementOnFolderUseCase.execute(folder, eventEnum);
    }

}
