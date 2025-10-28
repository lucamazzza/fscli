package ch.supsi.fscli.backend.util;

import java.util.logging.Logger;

public class FilesystemLogger {
    private static final Logger LOGGER = Logger.getLogger("Filesystem");

    public static void logInfo(String message) {
        LOGGER.info(message);
    }
    public static void logError(String message) {
        LOGGER.severe(message);
    }
}
