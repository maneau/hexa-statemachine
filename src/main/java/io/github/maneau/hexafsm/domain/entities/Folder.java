package io.github.maneau.hexafsm.domain.entities;

import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.StateEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Folder {
    UUID id;
    String name;
    LocalDateTime updateTime;
    Boolean isLocked;
    LocalDateTime lockTime;
    StateEnum state;
}
