package io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.EventTypeEnum.*;
import static io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.StateEnum.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum LinkEnum {
    INITIALIZE("Initialize", INIT, NEW, ERROR, EVT_EDIT, List.of(f -> LogAction.getInstance().execute(f))),
    IMPOSSIBLE("Impossible", NEW, INIT, ERROR, EVT_INIT, List.of(new ImpossibleAction("IMPOSSIBLE action"))),
    DELETE("Delete", NEW, DELETED, ERROR, EVT_DELETE, List.of(f -> LogAction.getInstance().execute(f))),
    BENCH1_2("BENCH1_2", BENCH_START, BENCH2, ERROR, EVT_BENCH1_OK, List.of(new LongTimeAction("long time action BENCH1_2"))),
    BENCH1_9("BENCH1_9", BENCH_START, BENCH_END, ERROR, EVT_BENCH19_OK, List.of(new LongTimeAction("long time action BENCH2_3"))),
    BENCH2_3("BENCH2_3", BENCH2, BENCH3, ERROR, EVT_BENCH2_OK, List.of(new LongTimeAction("long time action BENCH2_3"))),
    BENCH3_4("BENCH3_4", BENCH3, BENCH4, ERROR, EVT_BENCH3_OK, List.of(new LongTimeAction("long time action BENCH3_4"))),
    BENCH4_5("BENCH4_5", BENCH4, BENCH5, ERROR, EVT_BENCH4_OK, List.of(new LongTimeAction("long time action BENCH4_5"))),
    BENCH5_6("BENCH5_6", BENCH5, BENCH6, ERROR, EVT_BENCH5_OK, List.of(new LongTimeAction("long time action BENCH5_6"))),
    BENCH6_7("BENCH6_7", BENCH6, BENCH7, ERROR, EVT_BENCH6_OK, List.of(new LongTimeAction("long time action BENCH6_7"))),
    BENCH7_8("BENCH7_8", BENCH7, BENCH8, ERROR, EVT_BENCH7_OK, List.of(new LongTimeAction("long time action BENCH7_8"))),
    BENCH8_9("BENCH8_9", BENCH8, BENCH_END, ERROR, EVT_BENCH8_OK, List.of(new LongTimeAction("long time action BENCH8_9")));

    private final String name;
    private final StateEnum from;
    private final StateEnum to;
    private final StateEnum failed;
    private final EventTypeEnum event;
    private final List<CallbackFunc<Folder>> actions;
}
