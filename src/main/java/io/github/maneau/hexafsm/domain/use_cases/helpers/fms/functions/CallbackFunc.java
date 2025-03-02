package io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions;

import io.github.maneau.hexafsm.domain.exceptions.TechException;

public interface CallbackFunc<T> {
    void execute(T entity) throws TechException;
}
