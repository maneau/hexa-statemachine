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
    private final static Map<UUID, Folder> mapDossiers = new HashMap<>();

    @Getter(lazy = true)
    private static final FolderInMemoryImpl instance = new FolderInMemoryImpl();

    @Override
    public Optional<Folder> get(UUID dossierId) {
        return Optional.ofNullable(mapDossiers.get(dossierId));
    }

    @Override
    public void save(Folder folder) {
        mapDossiers.put(folder.getId(), folder);
    }
}
