package ch.supsi.fscli.frontend.util;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;

public enum AppError {
    // --- SAVE OPERATIONS (100 Series) ---
    SAVE_SUCCESS(100, FrontendMessageProvider.get("filesystem.save.success")),
    SAVE_FAILED_FILE_NOT_FOUND(101, FrontendMessageProvider.get("error.noFileSelectedSave")),
    SAVE_FAILED_GENERIC(102, FrontendMessageProvider.get("event.unknownError")),

    // --- LOAD OPERATIONS (200 Series) ---
    LOAD_SUCCESS(200, FrontendMessageProvider.get("filesystem.loaded")),
    LOAD_FAILED_NOT_FOUND(201, FrontendMessageProvider.get("filesystem.notLoaded")),
    LOAD_FAILED_CORRUPT(202, FrontendMessageProvider.get("fileEvent.invalidLoadFormat")),
    LOAD_FAILED_READ(203, "fileEvent.invalidLoadRead"),

    // --- NEW FILE OPERATIONS (300 Series) ---
    NEW_SUCCESS(300, FrontendMessageProvider.get("filesystem.newCreated")),
    NEW_FAILED_BS_MISSING(301, FrontendMessageProvider.get("fileEvent.BackendServiceMissing")),
    NEW_FAILED_UNSAVED_WORK(302, FrontendMessageProvider.get("fileEvent.UnsavedWork")),

    // --- SAVE AS OPERATIONS (400 Series) ---
    SAVE_AS_SUCCESS(400, FrontendMessageProvider.get("filesystem.newCreated")),
    SAVE_AS_FAILED_ALREADY_EXISTS(401, FrontendMessageProvider.get("error.fileExists")),
    SAVE_AS_FAILED_INVALID_PATH(402, FrontendMessageProvider.get("error.invalidPath")),

    // --- COMMAND EXECUTION OPERATIONS (500 Series) ---
    CMD_EXECUTION_SUCCESS(500, "Command executed successfully."),
    CMD_EXECUTION_FAILED_FS_MISSING(501, FrontendMessageProvider.get("command.Notsuccessful")),
    CMD_EXECUTION_FAILED_BAD_RESPONSE(502, FrontendMessageProvider.get("command.NotsuccessfulBad"));

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
