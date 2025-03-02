package io.github.maneau.hexafsm.domain.use_cases.helpers.evts;

import io.github.maneau.hexafsm.domain.entities.Event;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions.CallbackFunc;

public interface EventQueuePersistant {
    void publish(Event event);

    void subscribe(CallbackFunc<Event> callbackEventFunc);

    Integer count();
}
