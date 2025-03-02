package io.github.maneau.hexafsm.domain.use_cases.folder;

import io.github.maneau.hexafsm.domain.entities.Folder;

import java.util.Optional;
import java.util.UUID;

public interface FolderPersistance {

    Optional<Folder> get(UUID dossierId);

    void save(Folder folder);
}
