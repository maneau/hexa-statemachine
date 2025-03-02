package io.github.maneau.hexafsm.infrastructure;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.use_cases.folder.FolderPersistance;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FolderInMemoryImpl implements FolderPersistance {
    private final static Map<UUID, Folder> mapFolders = new HashMap<>();

    @Getter(lazy = true)
    private static final FolderInMemoryImpl instance = new FolderInMemoryImpl();

    @Override
    public Optional<Folder> get(UUID folderId) {
        return Optional.ofNullable(mapFolders.get(folderId));
    }

    @Override
    public void save(Folder folder) {
        mapFolders.put(folder.getId(), folder);
    }
}
