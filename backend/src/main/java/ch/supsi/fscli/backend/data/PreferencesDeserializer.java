package ch.supsi.fscli.backend.data;

import ch.supsi.fscli.backend.model.UserPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class PreferencesDeserializer {
    private final ObjectMapper mapper = new ObjectMapper();

    public UserPreferences deserialize(String json) throws IOException {
        return mapper.readValue(json, UserPreferences.class);
    }
}
