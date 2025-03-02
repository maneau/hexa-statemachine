package io.github.maneau.hexafsm.domain.use_cases.folder;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.infrastructure.FolderInMemoryImpl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetFolderUseCase {
    @Getter(lazy = true)
    private static final GetFolderUseCase instance = new GetFolderUseCase();

    final FolderPersistance folderPersistance = FolderInMemoryImpl.getInstance();

    public Optional<Folder> execute(UUID folderId) {
        return folderPersistance.get(folderId);
    }
}
