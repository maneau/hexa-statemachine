package io.github.maneau.hexafsm.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class FolderLock {
    LocalDateTime lockTime;
    UUID folderId;
}
