package ch.supsi.fscli.backend.data;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PreferencesFileManager {
    private static final Path PREF_PATH = Paths.get(System.getProperty("user.home"), "fs_prefs.json");
    private static final PreferencesSerializer serializer = new PreferencesSerializer();
    private static final PreferencesDeserializer deserializer = new PreferencesDeserializer();

}
