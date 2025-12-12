package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.FileSystemController;
import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.serde.FilesystemFileManager;
import ch.supsi.fscli.frontend.event.CommandLineEvent;
import ch.supsi.fscli.frontend.event.EventPublisher;
import ch.supsi.fscli.frontend.event.FileSystemEvent;
import ch.supsi.fscli.frontend.util.AppError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileSystemModelTest {
    @Mock private FileSystemPersistenceController mockPersistence;
    @Mock private FileSystemController mockController;
    @Mock private EventPublisher<FileSystemEvent> mockFsPublisher;
    @Mock private EventPublisher<CommandLineEvent> mockCmdPublisher;
    @Mock private FilesystemFileManager mockFileManager;
    @Mock private FileSystem mockFileSystem;
    @Mock private DirectoryNode mockRootNode;
    @Mock private File mockFile;

    private FileSystemModel model;

    @BeforeEach
    void setUp() {
        model = spy(new FileSystemModel(() -> mockController));
        model.setBackendPersistenceController(mockPersistence);
        model.setFileSystemEventManager(mockFsPublisher);
        model.setCommandLineEventManager(mockCmdPublisher);
        lenient().when(mockFile.toPath()).thenReturn(Path.of("test.fs"));
    }

    @Nested
    @DisplayName("Tests for save()")
    class SaveTests {
        @Test
        @DisplayName("Should fail if file is not set (Save without Open)")
        void saveFailNoFile() {
            model.save();
            assertErrorEvent(AppError.SAVE_FAILED_FILE_NOT_FOUND);
            verifyNoInteractions(mockFileManager);
        }

        @Test
        @DisplayName("Should save successfully when file exists")
        void saveSuccess() throws IOException {
            setupModelWithLoadedFile();
            doReturn(mockFileManager).when(model).createFileManager(any());
            when(mockPersistence.getFileSystem()).thenReturn(mockFileSystem);
            when(mockFileSystem.getRoot()).thenReturn(mockRootNode);
            model.save();
            verify(mockFileManager).save(mockRootNode);
            assertErrorEvent(AppError.SAVE_SUCCESS);
        }

        @Test
        @DisplayName("Should handle IOException during save")
        void saveIoException() throws IOException {
            setupModelWithLoadedFile();
            doReturn(mockFileManager).when(model).createFileManager(any());
            when(mockPersistence.getFileSystem()).thenReturn(mockFileSystem);
            when(mockFileSystem.getRoot()).thenReturn(mockRootNode);
            doThrow(new IOException()).when(mockFileManager).save(any());
            model.save();
            assertErrorEvent(AppError.SAVE_FAILED_GENERIC);
        }
    }

    @Nested
    @DisplayName("Tests for saveAs(File)")
    class SaveAsTests {
        @Test
        @DisplayName("Should fail if target file is null")
        void saveAsNull() {
            model.saveAs(null);
            assertErrorEvent(AppError.SAVE_AS_FAILED_INVALID_PATH);
        }

        @Test
        @DisplayName("Should save to new file successfully")
        void saveAsSuccess() throws IOException {
            doReturn(mockFileManager).when(model).createFileManager(any());
            when(mockPersistence.getFileSystem()).thenReturn(mockFileSystem);
            when(mockFileSystem.getRoot()).thenReturn(mockRootNode);
            model.saveAs(mockFile);
            verify(mockFileManager).save(mockRootNode);
            assertErrorEvent(AppError.SAVE_AS_SUCCESS);
        }
    }

    @Nested
    @DisplayName("Tests for load(File)")
    class LoadTests {
        @Test
        @DisplayName("Should fail silently (or return) if file is null/non-existent")
        void loadInvalidFile() {
            when(mockFile.exists()).thenReturn(false);
            model.load(mockFile);
            verifyNoInteractions(mockPersistence);
            verifyNoInteractions(mockFsPublisher);
        }

        @Test
        @DisplayName("Should succeed when persistence loads file")
        void loadSuccess() {
            when(mockFile.exists()).thenReturn(true);
            when(mockPersistence.loadFileSystem(any())).thenReturn(true);
            when(mockPersistence.isFileSystemLoaded()).thenReturn(true);
            model.load(mockFile);
            assertTrue(model.isFileSystemReady(), "File System should be ready after load");
            assertErrorEvent(AppError.LOAD_SUCCESS);
        }

        @Test
        @DisplayName("Should fail if persistence returns false")
        void loadFailure() {
            when(mockFile.exists()).thenReturn(true);
            when(mockPersistence.loadFileSystem(any())).thenReturn(false);
            model.load(mockFile);
            assertErrorEvent(AppError.LOAD_FAILED_READ);
        }
    }

    @Nested
    @DisplayName("Tests for createFileSystem()")
    class CreateFileSystemTests {
        @Test
        @DisplayName("Should fail if unsaved work exists and force is false")
        void createFailUnsaved() {
            when(mockFile.exists()).thenReturn(true);
            when(mockPersistence.loadFileSystem(any())).thenReturn(true);
            when(mockPersistence.isFileSystemLoaded()).thenReturn(true);
            model.load(mockFile);
            clearInvocations(mockFsPublisher);
            try {
                java.lang.reflect.Field f = FileSystemModel.class.getDeclaredField("file");
                f.setAccessible(true);
                f.set(model, null);
            } catch (Exception e) {
                fail("Reflection failed");
            }
            model.createFileSystem(false);
            assertErrorEvent(AppError.NEW_FAILED_UNSAVED_WORK);
        }

        @Test
        @DisplayName("Should succeed and create new FS")
        void createSuccess() {
            when(mockPersistence.getFileSystem()).thenReturn(mockFileSystem);
            model.createFileSystem(true);
            verify(mockPersistence).createNewFileSystem();
            assertErrorEvent(AppError.NEW_SUCCESS);
        }
    }

    @Nested
    @DisplayName("Tests for executeCommand()")
    class ExecuteCommandTests {

        @Test
        @DisplayName("Should fail if FS is not ready")
        void executeNotReady() {
            CommandResponseDTO response = model.executeCommand("ls");
            assertFalse(response.isSuccess());
            verify(mockCmdPublisher).notify(argThat(e -> e.error() == AppError.CMD_EXECUTION_FAILED_FS_MISSING));
        }

        @Test
        @DisplayName("Should execute successfully")
        void executeSuccess() {
            setupModelWithLoadedFile();
            when(mockPersistence.isFileSystemLoaded()).thenReturn(true);
            when(mockPersistence.getCurrentDirectory()).thenReturn("/root");
            CommandResponseDTO mockResponse = mock(CommandResponseDTO.class);
            when(mockResponse.getOutput()).thenReturn(List.of("file1.txt")); // Return a List!
            when(mockResponse.getOutputAsString()).thenReturn("file1.txt");  // Return String for event
            when(mockResponse.getErrorMessage()).thenReturn(null);
            when(mockController.executeCommand("ls")).thenReturn(mockResponse);
            CommandResponseDTO result = model.executeCommand("ls");
            assertTrue(result.isSuccess());
            assertEquals(List.of("file1.txt"), result.getOutput());
        }
    }

    private void setupModelWithLoadedFile() {
        when(mockFile.exists()).thenReturn(true);
        when(mockPersistence.loadFileSystem(any())).thenReturn(true);
        model.load(mockFile);
        clearInvocations(mockFsPublisher);
    }

    private void assertErrorEvent(AppError expectedError) {
        ArgumentCaptor<FileSystemEvent> captor = ArgumentCaptor.forClass(FileSystemEvent.class);
        verify(mockFsPublisher).notify(captor.capture());
        assertEquals(expectedError, captor.getValue().error());
    }
}