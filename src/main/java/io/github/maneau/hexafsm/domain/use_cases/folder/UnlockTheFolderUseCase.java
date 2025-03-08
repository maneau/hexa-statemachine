package io.github.maneau.hexafsm.domain.use_cases.folder;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.entities.FolderLock;
import io.github.maneau.hexafsm.domain.exceptions.DataIntegrityException;
import io.github.maneau.hexafsm.domain.exceptions.TechException;
import io.github.maneau.hexafsm.infrastructure.FolderInMemoryImpl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UnlockTheFolderUseCase {
    public static final boolean UNLOCK = false;

    @Getter(lazy = true)
    private static final UnlockTheFolderUseCase instance = new UnlockTheFolderUseCase();

    final FolderPersistance folderPersistance = FolderInMemoryImpl.getInstance();
    final GetFolderUseCase getFolderUseCase = GetFolderUseCase.getInstance();

    public void execute(@NonNull FolderLock folderLock) {
        Folder folder = getFolderUseCase.execute(folderLock.getFolderId())
                .orElseThrow(() -> new TechException("Folder id not founded" + folderLock.getFolderId()));
        if (UNLOCK == folder.getIsLocked()) {
            if(folder.getLockTime() != folderLock.getLockTime()) {
                log.error("Integrity failed, the lock date has change on folder '{}'", folder.getName());
                throw new DataIntegrityException("Integrity failed, the lock date has change");
            } else {
                log.debug("Integrity is ok, the lock date has not changed on folder '{}'", folder.getName());
            }
        } else {
            folder.setIsLocked(UNLOCK);
            folder.setLockTime(LocalDateTime.now());
            folderPersistance.save(folder);
        }
    }
}
