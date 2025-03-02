package io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.exceptions.TechException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LogAction implements CallbackFunc<Folder> {

    private final String actionName;

    @Override
    public void execute(Folder folder) throws TechException {
        log.info("folder {} action {}", folder.getName(), actionName);
    }
}
