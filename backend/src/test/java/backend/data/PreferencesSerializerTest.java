package backend.data;

import ch.supsi.fscli.backend.data.PreferencesSerializer;
import ch.supsi.fscli.backend.model.UserPreferences;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreferencesSerializerTest {

    @Test
    void testSerialize() throws Exception {
        // crea le preferenze con valori fuori limite per testare il clamp
        UserPreferences prefs = new UserPreferences();
        prefs.setLanguage("it");
        prefs.setCmdColumns(200);
        prefs.setOutputLines(1);
        prefs.setLogLines(200);
        prefs.setCmdFont("Monospaced");
        prefs.setOutputFont("Monospaced");
        prefs.setLogFont("SansSerif");

        PreferencesSerializer serializer = new PreferencesSerializer();
        String json = serializer.serialize(prefs);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        assertEquals("it", node.get("language").asText());
        assertEquals(100, node.get("cmdColumns").asInt());
        assertEquals(3, node.get("outputLines").asInt());
        assertEquals(100, node.get("logLines").asInt());

        assertTrue(node.has("cmdFont"));
        assertTrue(node.has("outputFont"));
        assertTrue(node.has("logFont"));
    }
}