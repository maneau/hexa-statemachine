package io.github.maneau.hexafsm.domain.use_cases.helpers.fms.functions;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.exceptions.TechException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LongTimeAction implements CallbackFunc<Folder> {

    public static final int SLEEP_TIME = 2000;
    private final String actionName;

    @Override
    public void execute(Folder folder) throws TechException {
        log.info("folder {} action start {}", folder.getName(), actionName);
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        log.info("folder {} action ended {}", folder.getName(), actionName);
    }
}
