package backend.service;

import ch.supsi.fscli.backend.data.serde.PreferencesFileManager;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.backend.service.PreferencesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesServiceTest {

    private Path tempFile;
    private PreferencesFileManager fileManager;
    private PreferencesService service;

    @BeforeEach
    void setup() throws IOException {
        tempFile = Files.createTempFile("prefs", ".json");
        fileManager = new PreferencesFileManager(tempFile);
        service = new PreferencesService(fileManager);
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testUpdatePreferenceClampsValuesAndSaves() throws IOException {
        service.updatePreference(p -> {
            p.setCmdColumns(200);
            p.setOutputLines(1);
            p.setLogLines(200);
        });

        UserPreferences prefs = service.getCurrentPrefs();

        assertEquals(100, prefs.getCmdColumns());
        assertEquals(3, prefs.getOutputLines());
        assertEquals(100, prefs.getLogLines());

        PreferencesFileManager newManager = new PreferencesFileManager(tempFile);
        UserPreferences loaded = newManager.load().orElseThrow();
        assertEquals(100, loaded.getCmdColumns());
        assertEquals(3, loaded.getOutputLines());
        assertEquals(100, loaded.getLogLines());
    }

    @Test
    void testReloadUpdatesCurrentPrefs() throws IOException {
        UserPreferences loadedPrefs = new UserPreferences();
        loadedPrefs.setLanguage("it");
        fileManager.save(loadedPrefs);

        service.reload();
        assertEquals("it", service.getCurrentPrefs().getLanguage());
    }
}
