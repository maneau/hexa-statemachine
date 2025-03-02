package io.github.maneau.hexafsm.domain.use_cases.folder;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.StateEnum;
import io.github.maneau.hexafsm.infrastructure.FolderInMemoryImpl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateFolderUseCase {
    @Getter(lazy = true)
    private static final CreateFolderUseCase instance = new CreateFolderUseCase();

    final FolderPersistance folderPersistance = FolderInMemoryImpl.getInstance();

    public Folder execute(String name) {
        Folder folder = Folder.builder()
                .id(UUID.randomUUID())
                .state(StateEnum.INIT)
                .updateTime(LocalDateTime.now())
                .isLocked(Boolean.FALSE)
                .name(name)
                .build();

        folderPersistance.save(folder);

        return folder;
    }
}
