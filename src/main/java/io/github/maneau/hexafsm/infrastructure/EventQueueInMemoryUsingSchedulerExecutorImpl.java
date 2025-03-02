package io.github.maneau.hexafsm.infrastructure;

import io.github.maneau.hexafsm.domain.entities.Event;
import io.github.maneau.hexafsm.domain.use_cases.helpers.evts.EventQueuePersistant;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.CallbackFunc;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class EventQueueInMemoryUsingSchedulerExecutorImpl implements EventQueuePersistant {
    public static final int N_THREADS = 20;
    private final Queue<Event> queue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
    private final List<CallbackFunc<Event>> callbackFuncs = new ArrayList<>();

    @Getter(lazy = true)
    private static final EventQueueInMemoryUsingSchedulerExecutorImpl instance = new EventQueueInMemoryUsingSchedulerExecutorImpl();

    private EventQueueInMemoryUsingSchedulerExecutorImpl() {
        try (ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1)) {
            scheduler.scheduleAtFixedRate(this::processEvents, 0, 1, TimeUnit.SECONDS);
        }
    }

    @Override
    public void publish(Event event) {
        queue.offer(event);
    }

    private void processEvents() {
        Event event;
        while ((event = queue.poll()) != null) {
            Event finalEvent = event;
            executorService.submit(() -> handleEvent(finalEvent));
        }
    }

    private void handleEvent(Event event) {
        callbackFuncs.forEach(f -> f.execute(event));
    }

    @Override
    public void subscribe(CallbackFunc<Event> callbackEventFunc) {
        this.callbackFuncs.add(callbackEventFunc);
    }

    @Override
    public Integer count() {
        return queue.size();
    }

    @Override
    public void shutdown() {

    }
}
