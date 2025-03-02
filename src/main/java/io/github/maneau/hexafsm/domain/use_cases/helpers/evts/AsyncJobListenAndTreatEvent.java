package io.github.maneau.hexafsm.domain.use_cases.helpers.evts;

import io.github.maneau.hexafsm.domain.entities.Event;
import io.github.maneau.hexafsm.domain.use_cases.ExecuteEventOnFolderUseCase;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.CallbackFunc;
import io.github.maneau.hexafsm.infrastructure.EventQueueInMemoryUsingThreadImpl;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;

@Slf4j
public class AsyncJobListenAndTreatEvent implements CallbackFunc<Event> {
    final EventQueuePersistant eventQueuePersistant = EventQueueInMemoryUsingThreadImpl.getInstance();
    final ExecuteEventOnFolderUseCase executeEventOnFolderUseCase = ExecuteEventOnFolderUseCase.getInstance();

    private static AsyncJobListenAndTreatEvent instance = null;

    public static void initialize() {
        if(isNull(instance)) {
            instance = new AsyncJobListenAndTreatEvent();
        }
    }

    public static void finish() {
        if(!isNull(instance)) {
            instance.eventQueuePersistant.shutdown();
        }
    }

    private AsyncJobListenAndTreatEvent() {
        eventQueuePersistant.subscribe(this);
    }

    @Override
    public void execute(Event event) {
        log.debug("Execute event {} on folder {}", event.getType(), event.getFolderId());
        long startTime = System.currentTimeMillis();

        executeEventOnFolderUseCase.execute(event.getFolderId(), event.getType());

        long duration = System.currentTimeMillis() - startTime;
        log.info("Execution of event {} on folder {} takes {}ms", event.getType(), event.getFolderId(), duration);
    }
}
