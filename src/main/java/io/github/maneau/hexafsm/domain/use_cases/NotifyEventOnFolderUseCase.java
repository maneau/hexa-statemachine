package io.github.maneau.hexafsm.domain.use_cases;

import io.github.maneau.hexafsm.domain.entities.Event;
import io.github.maneau.hexafsm.domain.use_cases.helpers.evts.EventQueuePersistant;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.EventTypeEnum;
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

    public void execute(@NonNull UUID folderId, @NonNull EventTypeEnum eventTypeEnum) {
        Event event = Event.builder()
                .date(LocalDateTime.now())
                .folderId(folderId)
                .type(eventTypeEnum)
                .build();
        eventQueuePersistant.publish(event);
    }
}
