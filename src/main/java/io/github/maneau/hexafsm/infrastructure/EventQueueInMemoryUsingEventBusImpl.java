package io.github.maneau.hexafsm.infrastructure;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import io.github.maneau.hexafsm.domain.entities.Event;
import io.github.maneau.hexafsm.domain.use_cases.helpers.evts.EventQueuePersistant;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.CallbackFunc;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventQueueInMemoryUsingEventBusImpl implements EventQueuePersistant {
    public static final int N_THREADS = 20;
    private final AsyncEventBus eventBus = new AsyncEventBus(Executors.newFixedThreadPool(N_THREADS));
    private final AtomicInteger eventCount = new AtomicInteger(0);

    @Getter(lazy = true)
    private static final EventQueueInMemoryUsingEventBusImpl instance = new EventQueueInMemoryUsingEventBusImpl();

    @Override
    public void publish(Event event) {
        eventBus.post(event);
        eventCount.incrementAndGet();
    }

    @Override
    public void subscribe(CallbackFunc<Event> callbackEventFunc) {
        eventBus.register(new Object() {
            @Subscribe
            public void handleEvent(Event event) {
                callbackEventFunc.execute(event);
                eventCount.decrementAndGet();
            }
        });
        eventBus.register(new Object() {
            @Subscribe
            public void handleEvent(Event event) {
                callbackEventFunc.execute(event);
                eventCount.decrementAndGet();
            }
        });
    }

    @Override
    public Integer count() {
        return eventCount.get();
    }

    @Override
    public void shutdown() {

    }
}
