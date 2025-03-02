package io.github.maneau.hexafsm.domain.use_cases;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.use_cases.folder.CreateFolderUseCase;
import io.github.maneau.hexafsm.domain.use_cases.folder.FolderPersistance;
import io.github.maneau.hexafsm.domain.use_cases.helpers.evts.AsyncJobListenAndTreatEvent;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.EventEnum;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.StateEnum;
import io.github.maneau.hexafsm.infrastructure.FolderInMemoryImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScenariiAsyncTest {
    final CreateFolderUseCase createFolderUseCase = CreateFolderUseCase.getInstance();
    final NotifyEventOnFolderUseCase notifyEventOnFolderUseCase = NotifyEventOnFolderUseCase.getInstance();

    final FolderPersistance folderPersistance = FolderInMemoryImpl.getInstance();

    @Test
    void createFolderThenEdit_should_successAfterDelay() throws InterruptedException {
        AsyncJobListenAndTreatEvent.initialize();
        // Given
        // When
        Folder folder = createFolderUseCase.execute("testAsync");
        assertThat(folderPersistance.get(folder.getId()).map(Folder::getState)).hasValue(StateEnum.INIT);

        notifyEventOnFolderUseCase.execute(folder.getId(), EventEnum.EVT_EDIT);

        // Then
        assertThat(folderPersistance.get(folder.getId()).map(Folder::getState)).hasValue(StateEnum.INIT);
        Thread.sleep(2000);
        assertThat(folderPersistance.get(folder.getId()).map(Folder::getState)).hasValue(StateEnum.NEW);
    }
}
