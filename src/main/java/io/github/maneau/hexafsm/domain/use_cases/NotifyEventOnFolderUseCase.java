package io.github.maneau.hexafsm.domain.use_cases;

import io.github.maneau.hexafsm.domain.entities.Event;
import io.github.maneau.hexafsm.domain.use_cases.helpers.evts.EventQueuePersistant;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.EventEnum;
import io.github.maneau.hexafsm.infrastructure.EventQueueInMemoryUsingThreadImpl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotifyEventOnFolderUseCase {

    @Getter(lazy = true)
    private static final NotifyEventOnFolderUseCase instance = new NotifyEventOnFolderUseCase();

    final EventQueuePersistant eventQueuePersistant = EventQueueInMemoryUsingThreadImpl.getInstance();

    public void execute(@NonNull UUID dossierId, @NonNull EventEnum eventEnum) {
        Event event = Event.builder()
                .date(LocalDateTime.now())
                .folderId(dossierId)
                .eventType(eventEnum)
                .build();
        eventQueuePersistant.publish(event);
    }
}
