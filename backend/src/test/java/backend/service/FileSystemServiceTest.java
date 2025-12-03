package backend.service;

import backend.util.TestInjectorFactory;
import ch.supsi.fscli.backend.controller.CommandResponse;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.service.CommandHistoryEntry;
import ch.supsi.fscli.backend.service.FileSystemService;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemServiceTest {

    private FileSystemService service;
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        Injector injector = TestInjectorFactory.createTestInjector();
        service = injector.getInstance(FileSystemService.class);
        fileSystem = injector.getInstance(FileSystem.class);
        service.setFileSystem(fileSystem);
    }

    @Test
    void testExecuteCommand() {
        CommandResponse response = service.executeCommand("test");
        
        assertNotNull(response);
        assertNotNull(response.getOutput());
    }

    @Test
    void testExecuteCommandAddsToHistory() {
        service.executeCommand("test");
        
        List<CommandHistoryEntry> history = service.getHistory();
        assertEquals(1, history.size());
        assertEquals("test", history.get(0).getCommand());
    }

    @Test
    void testExecuteCommandSilent() {
        CommandResponse response = service.executeCommandSilent("test");
        
        assertNotNull(response);
        List<CommandHistoryEntry> history = service.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    void testGetAvailableCommands() {
        String[] commands = service.getAvailableCommands();
        
        assertNotNull(commands);
    }

    @Test
    void testGetCommandHelp() {
        String help = service.getCommandHelp("test");
        
        assertNotNull(help);
        assertTrue(help.contains("test"));
    }

    @Test
    void testGetAllCommandsHelp() {
        List<String> helpTexts = service.getAllCommandsHelp();
        
        assertNotNull(helpTexts);
    }

    @Test
    void testGetHistoryInitiallyEmpty() {
        List<CommandHistoryEntry> history = service.getHistory();
        
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void testGetHistoryReturnsDefensiveCopy() {
        service.executeCommand("test");
        
        List<CommandHistoryEntry> history1 = service.getHistory();
        List<CommandHistoryEntry> history2 = service.getHistory();
        
        assertNotSame(history1, history2);
    }

    @Test
    void testGetHistoryCommands() {
        service.executeCommand("cmd1");
        service.executeCommand("cmd2");
        
        List<String> commands = service.getHistoryCommands();
        
        assertEquals(2, commands.size());
        assertEquals("cmd1", commands.get(0));
        assertEquals("cmd2", commands.get(1));
    }

    @Test
    void testClearHistory() {
        service.executeCommand("cmd1");
        service.executeCommand("cmd2");
        
        service.clearHistory();
        
        assertTrue(service.getHistory().isEmpty());
    }

    @Test
    void testGetLastCommands() {
        service.executeCommand("cmd1");
        service.executeCommand("cmd2");
        service.executeCommand("cmd3");
        
        List<CommandHistoryEntry> last2 = service.getLastCommands(2);
        
        assertEquals(2, last2.size());
        assertEquals("cmd2", last2.get(0).getCommand());
        assertEquals("cmd3", last2.get(1).getCommand());
    }

    @Test
    void testGetLastCommandsZero() {
        service.executeCommand("cmd1");
        
        List<CommandHistoryEntry> last0 = service.getLastCommands(0);
        
        assertTrue(last0.isEmpty());
    }

    @Test
    void testGetLastCommandsMoreThanAvailable() {
        service.executeCommand("cmd1");
        
        List<CommandHistoryEntry> last100 = service.getLastCommands(100);
        
        assertEquals(1, last100.size());
    }

    @Test
    void testSearchHistory() {
        service.executeCommand("mkdir test");
        service.executeCommand("cd test");
        service.executeCommand("ls");
        
        List<CommandHistoryEntry> results = service.searchHistory("test");
        
        assertEquals(2, results.size());
    }

    @Test
    void testSearchHistoryCaseInsensitive() {
        service.executeCommand("MKDIR DIR");
        
        List<CommandHistoryEntry> results = service.searchHistory("mkdir");
        
        assertEquals(1, results.size());
    }

    @Test
    void testSearchHistoryNoMatches() {
        service.executeCommand("ls");
        
        List<CommandHistoryEntry> results = service.searchHistory("mkdir");
        
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchHistoryEmptyPattern() {
        service.executeCommand("cmd1");
        service.executeCommand("cmd2");
        
        List<CommandHistoryEntry> results = service.searchHistory("");
        
        assertEquals(2, results.size());
    }

    @Test
    void testHistoryLimit() {
        for (int i = 0; i < 1100; i++) {
            service.executeCommand("cmd" + i);
        }
        
        List<CommandHistoryEntry> history = service.getHistory();
        assertEquals(1000, history.size());
    }

    @Test
    void testMultipleExecutions() {
        for (int i = 0; i < 10; i++) {
            service.executeCommand("cmd" + i);
        }
        
        assertEquals(10, service.getHistory().size());
    }

    @Test
    void testHistoryEntriesHaveTimestamps() {
        service.executeCommand("test");
        
        List<CommandHistoryEntry> history = service.getHistory();
        assertTrue(history.get(0).getTimestamp() > 0);
    }

    @Test
    void testSuccessfulAndFailedCommands() {
        service.executeCommand("validcmd");
        service.executeCommand("invalidcmd");
        
        assertEquals(2, service.getHistory().size());
    }

    @Test
    void testClearHistoryAndAddNew() {
        service.executeCommand("cmd1");
        service.clearHistory();
        service.executeCommand("cmd2");
        
        List<CommandHistoryEntry> history = service.getHistory();
        assertEquals(1, history.size());
        assertEquals("cmd2", history.get(0).getCommand());
    }
}
