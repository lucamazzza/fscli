package ch.supsi.fscli.backend.util;

import java.util.function.BiConsumer;

/**
 * Logger for user preferences operations.
 * Supports external listeners for log events.
 */
public class PreferencesLogger {
    private static BiConsumer<String, String> externalListener;

    public static void setExternalListener(BiConsumer<String, String> listener) {
        externalListener = listener;
    }

    public static void logInfo(String message) {
        if (externalListener != null) {
            externalListener.accept("INFO", message);
        }
    }

    public static void logError(String message, Exception e) {
        System.err.println("[ERROR] " + message);
        if (e != null) {
            e.printStackTrace();
        }
        if (externalListener != null) {
            String fullMessage = message + (e != null ? ": " + e.getMessage() : "");
            externalListener.accept("ERROR", fullMessage);
        }
    }
}