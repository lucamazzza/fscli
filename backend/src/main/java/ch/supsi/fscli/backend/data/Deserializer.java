package ch.supsi.fscli.backend.data;

import ch.supsi.fscli.backend.business.UserPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Deserializer<T> {
    private final ObjectMapper mapper = new ObjectMapper();

    public T deserialize(String json, Class<T> classType) throws IOException {
        return mapper.readValue(json, classType);
    }
}
