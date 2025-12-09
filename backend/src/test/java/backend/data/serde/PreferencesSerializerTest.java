package backend.data.serde;

import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.backend.data.serde.Serializer;
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

        Serializer<UserPreferences> serializer = new Serializer<>();
        String json = serializer.serialize(prefs);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        assertEquals("it", node.get("language").asText());
        assertEquals(80, node.get("cmdColumns").asInt());
        assertEquals(10, node.get("outputLines").asInt());
        assertEquals(5, node.get("logLines").asInt());

        assertTrue(node.has("cmdFont"));
        assertTrue(node.has("outputFont"));
        assertTrue(node.has("logFont"));
    }
}