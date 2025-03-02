package io.github.maneau.hexafsm.infrastructure;

import io.github.maneau.hexafsm.domain.entities.Event;
import io.github.maneau.hexafsm.domain.use_cases.helpers.evts.EventQueuePersistant;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.CallbackFunc;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventQueueInMemoryUsingThreadImpl implements EventQueuePersistant {
    private static final int N_THREADS = 20;
    private static final BlockingQueue<Event> queue = new LinkedBlockingQueue<>();

    @Getter(lazy = true)
    private static final EventQueueInMemoryUsingThreadImpl instance = new EventQueueInMemoryUsingThreadImpl();

    @Override
    public void publish(Event event) {
        queue.add(event);
    }

    @Override
    public void subscribe(CallbackFunc<Event> callbackEventFunc) {
        for(int i =0; i< N_THREADS; i++) {
            Thread workerThread = new Thread(new ConsumerWorker(queue, callbackEventFunc));
            workerThread.start();
        }
    }

    @Override
    public Integer count() {
        return queue.size();
    }

    static class ConsumerWorker implements Runnable {

        private final CallbackFunc<Event> callbackEventFunc;
        private final BlockingQueue<Event> queue;

        public ConsumerWorker(BlockingQueue<Event> queue, CallbackFunc<Event> callbackEventFunc) {
            this.callbackEventFunc = callbackEventFunc;
            this.queue = queue;
        }

        @Override
        public void run() {
            do {
                try {
                    Event event = queue.take();
                    callbackEventFunc.execute(event);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (Exception ignored) {
                }
            } while (true);
        }
    }
}
