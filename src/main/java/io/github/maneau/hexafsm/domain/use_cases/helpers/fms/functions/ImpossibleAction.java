package io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.exceptions.TechException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ImpossibleAction implements CallbackFunc<Folder>  {

    private final String cause;

    @Override
    public void execute(Folder folder) throws TechException {
        log.error("folder {} action {}", folder.getName(), cause);
        throw new TechException(cause);
    }
}
