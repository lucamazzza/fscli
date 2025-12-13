package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.FileSystemModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileSystemControllerTest {

    @Mock
    private FileSystemModel mockModel;

    @Mock
    private File mockFile;

    private FileSystemController controller;

    @BeforeEach
    void setUp() {
        controller = new FileSystemController();
        controller.setModel(mockModel);
    }

    @Test
    void testNewFileSystem() {
        controller.newFileSystem(true);
        verify(mockModel).createFileSystem(true);

        controller.newFileSystem(false);
        verify(mockModel).createFileSystem(false);
    }

    @Test
    void testSave() {
        controller.save();
        verify(mockModel).save();
    }

    @Test
    void testSaveAs() {
        controller.saveAs(mockFile);
        verify(mockModel).saveAs(mockFile);
        controller.saveAs(null);
        verifyNoMoreInteractions(mockModel);
    }

    @Test
    void testLoad() {
        controller.load(mockFile);
        verify(mockModel).load(mockFile);
        controller.load(null);
        verifyNoMoreInteractions(mockModel);
    }

    @Test
    void testExecuteCommandSanitization() {
        String dirtyCommand = "  ls -la   ";
        controller.executeCommand(dirtyCommand);
        verify(mockModel).executeCommand("ls -la");
    }

    @Test
    void testExecuteCommandValidation() {
        controller.executeCommand(null);
        controller.executeCommand("");
        controller.executeCommand("   ");
        verifyNoInteractions(mockModel);
    }
}