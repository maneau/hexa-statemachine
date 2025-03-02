package io.github.maneau.hexafsm.domain.entities;

import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.EventEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Event {
    private final LocalDateTime date;
    private final UUID folderId;
    private final EventEnum eventType;
}
