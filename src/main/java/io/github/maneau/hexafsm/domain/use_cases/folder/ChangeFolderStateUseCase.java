package io.github.maneau.hexafsm.domain.use_cases.folder;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.StateEnum;
import io.github.maneau.hexafsm.infrastructure.FolderInMemoryImpl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeFolderStateUseCase {
    @Getter(lazy = true)
    private static final ChangeFolderStateUseCase instance = new ChangeFolderStateUseCase();

    final FolderPersistance folderPersistance = FolderInMemoryImpl.getInstance();

    public void execute(@NonNull Folder folder, @NonNull StateEnum stateEnum) {

        folder.setState(stateEnum);
        folderPersistance.save(folder);

    }
}
