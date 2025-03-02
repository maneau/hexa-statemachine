package io.github.maneau.hexafsm.infrastructure;

import io.github.maneau.hexafsm.domain.entities.Event;
import io.github.maneau.hexafsm.domain.use_cases.helpers.evts.EventQueuePersistant;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.CallbackFunc;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventQueueInMemoryUsingThreadImpl implements EventQueuePersistant {
    private static final int N_THREADS = 20;
    private static final BlockingQueue<Event> queue = new LinkedBlockingQueue<>();
    private static final List<Thread> workerThreads = new ArrayList<>();

    @Getter(lazy = true)
    private static final EventQueueInMemoryUsingThreadImpl instance = new EventQueueInMemoryUsingThreadImpl();

    @Override
    public void publish(Event event) {
        queue.add(event);
    }

    @Override
    public void subscribe(CallbackFunc<Event> callbackEventFunc) {
        for (int i = 0; i <= N_THREADS; i++) {
            Thread workerThread = new Thread(new ConsumerWorker(i, queue, callbackEventFunc));
            workerThread.start();
            workerThreads.add(workerThread);
        }
    }

    @Override
    public Integer count() {
        return queue.size();
    }

    @Override
    public void shutdown() {
        workerThreads.forEach(Thread::interrupt);
    }

    static class ConsumerWorker implements Runnable {

        private final CallbackFunc<Event> callbackEventFunc;
        private final BlockingQueue<Event> queue;
        private final int threadNumber;

        public ConsumerWorker(int threadNumber, BlockingQueue<Event> queue, CallbackFunc<Event> callbackEventFunc) {
            this.threadNumber = threadNumber;
            this.callbackEventFunc = callbackEventFunc;
            this.queue = queue;
        }

        @Override
        public void run() {
            log.info("Starting thread#{}", threadNumber);
            do {
                try {
                    Event event = queue.take();
                    callbackEventFunc.execute(event);
                } catch (InterruptedException e) {
                    log.warn("Thread#{} interrupt called {}", threadNumber, e.getMessage());
                    Thread.currentThread().interrupt();
                    return;
                } catch (Exception e) {
                    log.error("Thread#{} exception {}", threadNumber, e.getMessage());
                }
            } while (true);
        }
    }
}
