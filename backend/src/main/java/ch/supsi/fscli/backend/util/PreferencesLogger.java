package ch.supsi.fscli.backend.util;

import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreferencesLogger {
    private static final Logger LOGGER = Logger.getLogger("Preferences");

    // Listener esterno (es. frontend)
    private static BiConsumer<Level, String> externalListener;

    public static void setExternalListener(BiConsumer<Level, String> listener) {
        externalListener = listener;
    }

    public static void logInfo(String msg) {
        LOGGER.log(Level.INFO, msg);
        notifyExternal(Level.INFO, msg);
    }

    public static void logError(String msg, Exception e) {
        LOGGER.log(Level.SEVERE, msg, e);
        notifyExternal(Level.SEVERE, msg + " (" + e.getMessage() + ")");
    }

    private static void notifyExternal(Level level, String msg) {
        if (externalListener != null) {
            externalListener.accept(level, msg);
        }
    }
}
