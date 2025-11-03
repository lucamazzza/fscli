package ch.supsi.fscli.backend.data.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class Deserializer<T> {
    private final ObjectMapper mapper;

    public Deserializer() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public T deserialize(String json, Class<T> classType) throws IOException {
        return mapper.readValue(json, classType);
    }
}
