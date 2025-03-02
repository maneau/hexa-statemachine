package io.github.maneau.hexafsm.domain.use_cases;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.use_cases.folder.CreateFolderUseCase;
import io.github.maneau.hexafsm.domain.use_cases.folder.FolderPersistance;
import io.github.maneau.hexafsm.domain.use_cases.helpers.evts.EventQueuePersistant;
import io.github.maneau.hexafsm.domain.use_cases.helpers.evts.AsyncJobListenAndTreatEvent;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.EventTypeEnum;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.StateEnum;
import io.github.maneau.hexafsm.infrastructure.FolderInMemoryImpl;
import io.github.maneau.hexafsm.infrastructure.EventQueueInMemoryUsingThreadImpl;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class AsyncBenchmarkMain {
    private static final int N_THREADS = 20; // Nombre de threads pour le traitement en parallèle
    private static final Random RANDOM = new Random();

    private static final List<EventTypeEnum> EVENT_ENUMS = List.of(EventTypeEnum.EVT_BENCH1_OK, EventTypeEnum.EVT_BENCH2_OK,
            EventTypeEnum.EVT_BENCH3_OK, EventTypeEnum.EVT_BENCH4_OK, EventTypeEnum.EVT_BENCH5_OK, EventTypeEnum.EVT_BENCH6_OK,
            EventTypeEnum.EVT_BENCH7_OK, EventTypeEnum.EVT_BENCH8_OK, EventTypeEnum.EVT_BENCH19_OK);

    private static final CreateFolderUseCase CREATE_DOSSIER_USE_CASE = CreateFolderUseCase.getInstance();
    private static final NotifyEventOnFolderUseCase NOTIFY_EVENT_ON_FOLDER_USE_CASE =
            NotifyEventOnFolderUseCase.getInstance();
    private static final FolderPersistance FOLDER_PERSISTANCE = FolderInMemoryImpl.getInstance();
    private static final EventQueuePersistant eventQueuePersistant = EventQueueInMemoryUsingThreadImpl.getInstance();

    private static final int maxKeyLength = 10;
    private static String title;

    public static List<UUID> init() {
        AsyncJobListenAndTreatEvent.initialize();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.ERROR);

        List<UUID> dossierIds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Folder folder = CREATE_DOSSIER_USE_CASE.execute("dossier bench " + i);
            folder.setState(StateEnum.BENCH_START);
            FOLDER_PERSISTANCE.save(folder);
            dossierIds.add(folder.getId());
        }

        title = Arrays.stream(StateEnum.values())
                .map(StateEnum::getName)
                .sorted()
                .map(AsyncBenchmarkMain::printCellule)
                .collect(Collectors.joining("\t"));

        return dossierIds;
    }

    private static String printCellule(String k) {
        return format("%-" + maxKeyLength + "s", k);
    }

    private static String printCellule(Integer value) {
        return format("%-" + maxKeyLength + "d", value);
    }

    public static void main(String[] args) throws Exception {

        List<UUID> dossierIds = init();

        long startTime = System.currentTimeMillis();
        ExecutorService eventProducer = executeEventProducer(dossierIds, EVENT_ENUMS);

        boolean isSuccess = waitUntilAllDossierInBench9orTimedOut(dossierIds, 5);

        conclusion(isSuccess, dossierIds, startTime);

        eventProducer.shutdown();
    }

    private static void conclusion(boolean isSuccess, List<UUID> dossierIds, long startTime) {

        long durationInMinutes = (System.currentTimeMillis() - startTime) / 1000 / 60;
        if(isSuccess) {
            System.out.println("--------------------------------------------------------------------");
            System.out.printf("| Bench ended successfully in %s minutes |\n", durationInMinutes);
            System.out.println("--------------------------------------------------------------------");
        } else {
            System.err.println("---------------------------------------------------------------------");
            System.err.printf("| Bench ended incompletely %d/%s dossier are finished in %s minutes |\n",
                    getNbDossiersInState(dossierIds, StateEnum.BENCH_END),
                    dossierIds.size(),
                    durationInMinutes);
            System.err.println("---------------------------------------------------------------------");
        }
    }

    private static boolean waitUntilAllDossierInBench9orTimedOut(List<UUID> dossierIds, int maxDurationInMinutes) throws InterruptedException {
        int cycle = 0;
        final int printFrequencyInSec = 5;
        final int nbCycleMax = maxDurationInMinutes * 60 / printFrequencyInSec;
        do {
            if (cycle % 10 == 0) {
                System.out.println(printCellule("Queue") + title);
            }
            cycle++;
            observeTask(dossierIds);
            Thread.sleep(1000 * printFrequencyInSec);
            if(getNbDossiersInState(dossierIds, StateEnum.BENCH_END) >= dossierIds.size()) {
                return true;
            }
        } while (cycle < nbCycleMax);
        return false;
    }

    private static long getNbDossiersInState(List<UUID> dossierIds, StateEnum state) {
        return dossierIds.stream()
                .map(FOLDER_PERSISTANCE::get)
                .flatMap(Optional::stream)
                .map(Folder::getState)
                .filter(s -> s == state)
                .count();
    }

    private static ExecutorService executeEventProducer(List<UUID> dossierIds, List<EventTypeEnum> eventTypeEnums) {
        Runnable sendTask = () -> {
            UUID randomDossierId = dossierIds.get(RANDOM.nextInt(dossierIds.size()));
            EventTypeEnum randomEvent = eventTypeEnums.get(RANDOM.nextInt(eventTypeEnums.size()));

            NOTIFY_EVENT_ON_FOLDER_USE_CASE.execute(randomDossierId, randomEvent);
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(N_THREADS);
        scheduler.scheduleAtFixedRate(sendTask, 0, 20, TimeUnit.MILLISECONDS);
        scheduler.schedule(scheduler::shutdown, 10, TimeUnit.MINUTES);
        return scheduler;
    }

    public static void observeTask(List<UUID> dossierIds) {
        Map<String, AtomicInteger> histogram = calculateHistogramOfDossiersStates(dossierIds);

        String values = histogram.values().stream()
                .map(v -> printCellule(v.get()))
                .collect(Collectors.joining("\t"));

        Integer queueSize = eventQueuePersistant.count();
        System.out.println(printCellule(queueSize) + values);
    }

    private static Map<String, AtomicInteger> calculateHistogramOfDossiersStates(List<UUID> dossierIds) {
        Map<String, AtomicInteger> histogram = new TreeMap<>();
        Arrays.stream(StateEnum.values()).forEach(s -> histogram.put(s.getName(), new AtomicInteger(0)));

        dossierIds.stream()
                .map(FOLDER_PERSISTANCE::get)
                .flatMap(Optional::stream)
                .map(dos -> dos.getState().getName())
                .forEach(state -> histogram.get(state).incrementAndGet());
        return histogram;
    }

}
