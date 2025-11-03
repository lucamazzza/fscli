package backend.data.serde;

import ch.supsi.fscli.backend.data.serde.PreferencesFileManager;
import ch.supsi.fscli.backend.core.UserPreferences;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesFileManagerTest {

    @Test
    void testSaveAndLoadWithClamp() throws IOException {
        Path tempFile = Files.createTempFile("prefs", ".json");
        PreferencesFileManager manager = new PreferencesFileManager(tempFile);

        UserPreferences prefs = new UserPreferences();
        prefs.setCmdColumns(200);
        prefs.setOutputLines(1);
        prefs.setLogLines(200);

        manager.save(prefs);
        Optional<UserPreferences> loaded = manager.load();

        assertTrue(loaded.isPresent());
        assertEquals(100, loaded.get().getCmdColumns());
        assertEquals(3, loaded.get().getOutputLines());
        assertEquals(100, loaded.get().getLogLines());

        Files.deleteIfExists(tempFile);
    }
}
