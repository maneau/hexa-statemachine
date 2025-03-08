package io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StateEnum {

    INIT("INIT"),
    NEW("NEW"),
    DELETED("DELETED"),
    ERROR("ERROR"),
    BENCH_START("BENCH1_STA"),
    BENCH2("BENCH2"),
    BENCH3("BENCH3"),
    BENCH4("BENCH4"),
    BENCH5("BENCH5"),
    BENCH6("BENCH6"),
    BENCH7("BENCH7"),
    BENCH8("BENCH8"),
    BENCH_END("BENCH9_END");

    private final String name;
}
