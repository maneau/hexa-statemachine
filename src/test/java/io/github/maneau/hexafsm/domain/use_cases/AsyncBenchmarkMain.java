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

/**
 * This bench creates NB_FOLDER_TO_BENCH folders and send random event to random folder.
 * The folders starts at State BENCH1_START and progress until BENCH9_END
 * This bench allow to improve multithreading and data integrity over the folder
 * The use of lock avoid corruption
 * Change the log level LOG_LEVEL = Level.WARN to view lock usage
 */
public class AsyncBenchmarkMain {
    private static final int MAX_DURATION_IN_MINUTES = 9;
    private static final int NB_FOLDER_TO_BENCH = 100;
    private static final int N_THREADS_FOR_SEND_EVENTS = 10;
    private static final int MAX_KEY_LENGTH = 11;
    private static final Random RANDOM = new Random();
    public static final Level LOG_LEVEL = Level.ERROR; // Set WARN to see Locks
    private static String title;

    private static final List<EventTypeEnum> EVENT_ENUMS = List.of(EventTypeEnum.EVT_BENCH1_OK, EventTypeEnum.EVT_BENCH2_OK,
            EventTypeEnum.EVT_BENCH3_OK, EventTypeEnum.EVT_BENCH4_OK, EventTypeEnum.EVT_BENCH5_OK, EventTypeEnum.EVT_BENCH6_OK,
            EventTypeEnum.EVT_BENCH7_OK, EventTypeEnum.EVT_BENCH8_OK, EventTypeEnum.EVT_BENCH19_OK);

    // Beans
    private static final CreateFolderUseCase createFolderUseCase = CreateFolderUseCase.getInstance();
    private static final NotifyEventOnFolderUseCase notifyEventOnFolderUseCase =
            NotifyEventOnFolderUseCase.getInstance();
    private static final FolderPersistance folderPersistance = FolderInMemoryImpl.getInstance();
    private static final EventQueuePersistant eventQueuePersistant = EventQueueInMemoryUsingThreadImpl.getInstance();

    public static void main(String[] args) throws Exception {
        List<UUID> dossierIds = init();
        long startTime = System.currentTimeMillis();

        ExecutorService eventProducer = executeEventProducer(dossierIds, EVENT_ENUMS);
        boolean isSuccess = waitUntilAllDossierInBench9orTimedOut(dossierIds, MAX_DURATION_IN_MINUTES);
        conclusion(isSuccess, dossierIds, startTime);
        finish(eventProducer);
    }

    private static void finish(ExecutorService eventProducer) {
        AsyncJobListenAndTreatEvent.finish();
        eventProducer.shutdownNow();
    }

    public static List<UUID> init() {
        AsyncJobListenAndTreatEvent.initialize();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(LOG_LEVEL);

        List<UUID> dossierIds = new ArrayList<>();
        for (int i = 0; i < NB_FOLDER_TO_BENCH; i++) {
            Folder folder = createFolderUseCase.execute("folder#" + i);
            folder.setState(StateEnum.BENCH_START);
            folderPersistance.save(folder);
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
        return format("%-" + MAX_KEY_LENGTH + "s", k);
    }

    private static String printCellule(Integer value) {
        return format("%-" + MAX_KEY_LENGTH + "d", value);
    }

    private static void conclusion(boolean isSuccess, List<UUID> dossierIds, long startTime) {

        long durationInMinutes = (System.currentTimeMillis() - startTime) / 1000 / 60;
        if (isSuccess) {
            System.out.println("--------------------------------------------------------------------");
            System.out.printf("| Bench ended successfully in %s minutes |\n", durationInMinutes);
            System.out.println("--------------------------------------------------------------------");
        } else {
            System.err.println("---------------------------------------------------------------------");
            System.err.printf("| Bench ended incompletely %d/%s dossier are finished in %s minutes |\n",
                    getNbDossiersInState(dossierIds, StateEnum.BENCH_END),
                    dossierIds.size(),
                    durationInMinutes);
            System.err.printf("| Folder with locks : %d/%d                       |\n",
                    countTheNumberOfLocks(dossierIds), dossierIds.size());
            System.err.println("---------------------------------------------------------------------");
        }
    }

    private static boolean waitUntilAllDossierInBench9orTimedOut(List<UUID> dossierIds, int maxDurationInMinutes) throws InterruptedException {
        int cycle = 0;
        final int printFrequencyInSec = 5;
        final int nbCycleMax = maxDurationInMinutes * 60 / printFrequencyInSec;
        do {
            if (cycle % 10 == 0) {
                System.out.println(printCellule("Progress") + printCellule("Queue") + title);
            }
            cycle++;
            observeTask(dossierIds, cycle, nbCycleMax);
            Thread.sleep(1000 * printFrequencyInSec);
            if (getNbDossiersInState(dossierIds, StateEnum.BENCH_END) >= dossierIds.size()) {
                observeTask(dossierIds, cycle, nbCycleMax);
                return true;
            }
        } while (cycle < nbCycleMax);
        return false;
    }

    private static long getNbDossiersInState(List<UUID> dossierIds, StateEnum state) {
        return dossierIds.stream()
                .map(folderPersistance::get)
                .flatMap(Optional::stream)
                .map(Folder::getState)
                .filter(s -> s == state)
                .count();
    }

    private static long countTheNumberOfLocks(List<UUID> dossierIds) {
        return dossierIds.stream()
                .map(folderPersistance::get)
                .flatMap(Optional::stream)
                .filter(Folder::getIsLocked)
                .count();
    }

    private static ExecutorService executeEventProducer(List<UUID> dossierIds, List<EventTypeEnum> eventTypeEnums) {
        Runnable sendTask = () -> {
            UUID randomDossierId = dossierIds.get(RANDOM.nextInt(dossierIds.size()));
            EventTypeEnum randomEvent = eventTypeEnums.get(RANDOM.nextInt(eventTypeEnums.size()));

            notifyEventOnFolderUseCase.execute(randomDossierId, randomEvent);
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(N_THREADS_FOR_SEND_EVENTS);
        scheduler.scheduleAtFixedRate(sendTask, 0, 20, TimeUnit.MILLISECONDS);
        scheduler.schedule(scheduler::shutdown, 10, TimeUnit.MINUTES);
        return scheduler;
    }

    public static void observeTask(List<UUID> dossierIds, int cycle, int nbCycleMax) {
        Map<String, AtomicInteger> histogram = calculateHistogramOfDossiersStates(dossierIds);

        String values = histogram.values().stream()
                .map(v -> printCellule(v.get()))
                .collect(Collectors.joining("\t"));

        Integer queueSize = eventQueuePersistant.count();
        System.out.println(printCellule(format("%d/%d", cycle, nbCycleMax)) + printCellule(queueSize) + values);
    }

    private static Map<String, AtomicInteger> calculateHistogramOfDossiersStates(List<UUID> dossierIds) {
        Map<String, AtomicInteger> histogram = new TreeMap<>();
        Arrays.stream(StateEnum.values()).forEach(s -> histogram.put(s.getName(), new AtomicInteger(0)));

        dossierIds.stream()
                .map(folderPersistance::get)
                .flatMap(Optional::stream)
                .map(dos -> dos.getState().getName())
                .forEach(state -> histogram.get(state).incrementAndGet());
        return histogram;
    }

}
