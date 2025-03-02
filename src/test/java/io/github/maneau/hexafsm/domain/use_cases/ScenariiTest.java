package io.github.maneau.hexafsm.domain.use_cases;

import io.github.maneau.hexafsm.domain.entities.Folder;
import io.github.maneau.hexafsm.domain.use_cases.folder.CreateFolderUseCase;
import io.github.maneau.hexafsm.domain.use_cases.folder.FolderPersistance;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.EventTypeEnum;
import io.github.maneau.hexafsm.domain.use_cases.helpers.fms.enums.StateEnum;
import io.github.maneau.hexafsm.infrastructure.FolderInMemoryImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScenariiTest {

    final CreateFolderUseCase createFolderUseCase = CreateFolderUseCase.getInstance();
    final ExecuteEventOnFolderUseCase executeEventOnFolderUseCase = ExecuteEventOnFolderUseCase.getInstance();
    final FolderPersistance folderPersistance = FolderInMemoryImpl.getInstance();

    @Test
    void createFolderThenEditIt() {
        // Given
        // When
        Folder folder = createFolderUseCase.execute("test");
        assertThat(folderPersistance.get(folder.getId()).map(Folder::getState)).hasValue(StateEnum.INIT);

        executeEventOnFolderUseCase.execute(folder.getId(), EventTypeEnum.EVT_EDIT);

        // Then
        assertThat(folderPersistance.get(folder.getId())).isNotEmpty();
        assertThat(folderPersistance.get(folder.getId()).map(Folder::getState)).hasValue(StateEnum.NEW);
    }

    @Test
    void createFolderThenDeleteIt_should_notBePossible() {
        // Given
        // When
        Folder folder = createFolderUseCase.execute("test");
        assertThat(folderPersistance.get(folder.getId()).map(Folder::getState)).hasValue(StateEnum.INIT);

        executeEventOnFolderUseCase.execute(folder.getId(), EventTypeEnum.EVT_DELETE);

        // Then
        assertThat(folderPersistance.get(folder.getId())).isNotEmpty();
        assertThat(folderPersistance.get(folder.getId()).map(Folder::getState)).hasValue(StateEnum.INIT);
    }

    @Test
    void createFolderThenEditAndInit_should_failed() {
        // Given
        // When
        Folder folder = createFolderUseCase.execute("testFailed");
        assertThat(folderPersistance.get(folder.getId()).map(Folder::getState)).hasValue(StateEnum.INIT);

        executeEventOnFolderUseCase.execute(folder.getId(), EventTypeEnum.EVT_EDIT);
        executeEventOnFolderUseCase.execute(folder.getId(), EventTypeEnum.EVT_INIT);

        // Then
        assertThat(folderPersistance.get(folder.getId())).isNotEmpty();
        assertThat(folderPersistance.get(folder.getId()).map(Folder::getState)).hasValue(StateEnum.ERROR);
    }
}