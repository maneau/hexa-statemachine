package io.github.maneau.hexafsm.domain.use_cases.folder;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.entities.FolderLock;
import io.github.maneau.hexafsm.infrastructure.FolderInMemoryImpl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TryToLockTheFolderUseCase {
    public static final boolean LOCK = true;

    @Getter(lazy = true)
    private static final TryToLockTheFolderUseCase instance = new TryToLockTheFolderUseCase();

    final FolderPersistance folderPersistance = FolderInMemoryImpl.getInstance();

    public Optional<FolderLock> execute(@NonNull Folder folder) {

        if(LOCK == folder.getIsLocked()) {
            return Optional.empty();
        } else {
            FolderLock lock = FolderLock.builder()
                    .folderId(folder.getId())
                    .lockTime(LocalDateTime.now())
                    .build();
            folder.setIsLocked(LOCK);
            folder.setLockTime(lock.getLockTime());
            folderPersistance.save(folder);
            return Optional.of(lock);
        }
    }
}
