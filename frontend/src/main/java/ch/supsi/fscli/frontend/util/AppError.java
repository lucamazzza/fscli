package ch.supsi.fscli.frontend.util;

public enum AppError {
    // --- SAVE OPERATIONS (100 Series) ---
    SAVE_SUCCESS(100, "Filesystem saved successfully."),
    SAVE_FAILED_FILE_NOT_FOUND(101, "Could not save: There is no saved file."),
    SAVE_FAILED_GENERIC(102, "An unknown error occurred while saving."),

    // --- LOAD OPERATIONS (200 Series) ---
    LOAD_SUCCESS(200, "File loaded successfully."),
    LOAD_FAILED_NOT_FOUND(201, "Could not load: File not found."),
    LOAD_FAILED_CORRUPT(202, "Could not load: File format is invalid."),
    LOAD_FAILED_READ(203, "Could not load: Read permission denied."),

    // --- NEW FILE OPERATIONS (300 Series) ---
    NEW_SUCCESS(300, "New filesystem created."),
    NEW_FAILED_BS_MISSING(301, "Could not create new filesystem: Backend service missing."),
    NEW_FAILED_UNSAVED_WORK(302, "Cannot create new filesystem: Unsaved work exists."),

    // --- SAVE AS OPERATIONS (400 Series) ---
    SAVE_AS_SUCCESS(400, "Filesystem saved successfully."),
    SAVE_AS_FAILED_ALREADY_EXISTS(401, "Could not save as: File already exists."),
    SAVE_AS_FAILED_INVALID_PATH(402, "Could not save as: The specified path is invalid."),

    // --- COMMAND EXECUTION OPERATIONS (500 Series) ---
    CMD_EXECUTION_SUCCESS(500, "Command executed successfully."),
    CMD_EXECUTION_FAILED_FS_MISSING(501, "Command execution failed: Filesystem is not initialized."),
    CMD_EXECUTION_FAILED_BAD_RESPONSE(502, "Command execution failed: Bad response");

    private final int errorCode;
    private final String defaultMessage;

    AppError(int errorCode, String defaultMessage) {
        this.errorCode = errorCode;
        this.defaultMessage = defaultMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
