package io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.StateEnum.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum LinkEnum {
    INITIALIZE("Initialize", INIT, NEW, ERROR, EventEnum.EVT_EDIT),
    IMPOSSIBLE("Impossible", NEW, INIT, ERROR, EventEnum.EVT_INIT),
    DELETE("Delete", NEW, DELETED, ERROR, EventEnum.EVT_DELETE),
    BENCH1_2("BENCH1_2", BENCH_START, BENCH2, ERROR, EventEnum.EVT_BENCH1_OK),
    BENCH1_9("BENCH1_9", BENCH_START, BENCH_END, ERROR, EventEnum.EVT_BENCH19_OK),
    BENCH2_3("BENCH2_3", BENCH2, BENCH3, ERROR, EventEnum.EVT_BENCH2_OK),
    BENCH3_4("BENCH3_4", BENCH3, BENCH4, ERROR, EventEnum.EVT_BENCH3_OK),
    BENCH4_5("BENCH4_5", BENCH4, BENCH5, ERROR, EventEnum.EVT_BENCH4_OK),
    BENCH5_6("BENCH5_6", BENCH5, BENCH6, ERROR, EventEnum.EVT_BENCH5_OK),
    BENCH6_7("BENCH6_7", BENCH6, BENCH7, ERROR, EventEnum.EVT_BENCH6_OK),
    BENCH7_8("BENCH7_8", BENCH7, BENCH_END, ERROR, EventEnum.EVT_BENCH7_OK),
    BENCH8_9("BENCH8_9", BENCH8, BENCH_END, ERROR, EventEnum.EVT_BENCH8_OK);

    private final String action;
    private final StateEnum from;
    private final StateEnum to;
    private final StateEnum failed;
    private final EventEnum event;
}
