package io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.CallbackFunc;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.ImpossibleAction;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.LogAction;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.LongTimeAction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StateEnum {

    INIT("INIT",
            List.of(new ImpossibleAction("Init is already the start action")),
            List.of(new LogAction("afterINIT"))
    ), NEW("NEW",
            List.of(new LogAction("beforeNEW")),
            List.of(new LogAction("afterINIT"))
    ), DELETED("DELETED",
            List.of(new LogAction("beforeDELETED")),
            List.of(new LogAction("afterDELETED"))
    ), ERROR("ERROR",
            List.of(new LogAction("beforeERROR")),
            List.of(new LogAction("afterERROR"))
    ), BENCH_START("BENCH1_START",
            List.of(new LongTimeAction("beforeBENCH1")),
            List.of(new LongTimeAction("afterBENCH1"))
    ), BENCH2("BENCH2",
            List.of(new LongTimeAction("beforeBENCH2")),
            List.of(new LongTimeAction("afterBENCH2"))
    ), BENCH3("BENCH3",
            List.of(new LongTimeAction("beforeBENCH3")),
            List.of(new LongTimeAction("afterBENCH3"))
    ), BENCH4("BENCH4",
            List.of(new LongTimeAction("beforeBENCH4")),
            List.of(new LongTimeAction("afterBENCH4"))
    ), BENCH5("BENCH5",
            List.of(new LongTimeAction("beforeBENCH5")),
            List.of(new LongTimeAction("afterBENCH5"))
    ), BENCH6("BENCH6",
            List.of(new LongTimeAction("beforeBENCH6")),
            List.of(new LongTimeAction("afterBENCH6"))
    ), BENCH7("BENCH7",
            List.of(new LongTimeAction("beforeBENCH7")),
            List.of(new LongTimeAction("afterBENCH7"))
    ), BENCH8("BENCH8",
            List.of(new LongTimeAction("beforeBENCH8")),
            List.of(new LongTimeAction("afterBENCH8"))
    ), BENCH_END("BENCH9_END",
            List.of(new LongTimeAction("beforeBENCH9")),
            List.of(new LongTimeAction("afterBENCH9"))
    );

    private final String name;
    private final List<CallbackFunc<Folder>> beforeFunc;
    private final List<CallbackFunc<Folder>> afterFunc;
}
