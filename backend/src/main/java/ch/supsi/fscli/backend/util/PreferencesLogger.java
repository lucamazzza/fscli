package ch.supsi.fscli.backend.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PreferencesLogger {
    private static final Logger LOGGER = Logger.getLogger("Preferences");

    public static void logInfo(String msg) {
        LOGGER.log(Level.INFO, msg);
    }

    public static void logError(String msg, Exception e) {
        LOGGER.log(Level.SEVERE, msg, e);
    }
}
