package io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.exceptions.TechException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogAction implements CallbackFunc<Folder> {

    @Getter(lazy = true)
    private static final LogAction instance = new LogAction();

    @Override
    public void execute(Folder folder) throws TechException {
        log.info("folder {} action", folder.getName());
    }
}
