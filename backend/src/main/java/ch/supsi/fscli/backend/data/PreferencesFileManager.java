package ch.supsi.fscli.backend.data;

import ch.supsi.fscli.backend.model.UserPreferences;
import ch.supsi.fscli.backend.util.PreferencesLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class PreferencesFileManager {
    private static final Path PREF_PATH = Paths.get(System.getProperty("user.home"), "fs_prefs.json");
    private static final PreferencesSerializer serializer = new PreferencesSerializer();
    private static final PreferencesDeserializer deserializer = new PreferencesDeserializer();

    public void save(UserPreferences prefs) throws IOException {
        Files.writeString(PREF_PATH, serializer.serialize(prefs));
    }

    public Optional<UserPreferences> load(){
        if(Files.exists(PREF_PATH))return Optional.empty();
        try{
            String json = Files.readString(PREF_PATH);
            return Optional.of(deserializer.deserialize(json));
        }catch(IOException e){
            PreferencesLogger.logError("Failed to load preferences", e);
            return Optional.empty();
        }
    }
}