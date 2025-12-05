package ch.supsi.fscli.backend.core;

import ch.supsi.fscli.backend.util.BackendGlobalVariables;

import java.util.List;
import java.util.Map;

public class UserPreferences {
    private String language = BackendGlobalVariables.DEFAULT_LANGUAGE;
    private int cmdColumns = BackendGlobalVariables.DEFAULT_CMD_COLUMNS;
    private int outputLines = BackendGlobalVariables.DEFAULT_OUTPUT_LINES;
    private int logLines = BackendGlobalVariables.DEFAULT_LOG_LINES;
    private String cmdFont = BackendGlobalVariables.DEFAULT_CMD_FONT;
    private String outputFont = BackendGlobalVariables.DEFAULT_OUTPUT_FONT;
    private String logFont = BackendGlobalVariables.DEFAULT_LOG_FONT;

    // flag che indica se durante il caricamento è stato fatto clamping/fallback
    private boolean clamped = false;

    public UserPreferences() {}

    public UserPreferences(UserPreferences other) {
        this.language = other.language;
        this.cmdColumns = other.cmdColumns;
        this.outputLines = other.outputLines;
        this.logLines = other.logLines;
        this.cmdFont = other.cmdFont;
        this.outputFont = other.outputFont;
        this.logFont = other.logFont;
        this.clamped = other.clamped;
    }

    // --- VALIDATOR METHODS --- (ora impostano clamped = true quando usano default)

    private int validateInt(Object input, int min, int max, int defaultValue) {
        if (input instanceof Number) {
            int value = ((Number) input).intValue();
            if (value >= min && value <= max) return value;
        }
        this.clamped = true;
        return defaultValue;
    }

    private String validateString(Object input, List<String> allowed, String defaultValue) {
        if (input instanceof String str) {
            if (allowed.contains(str)) return str;
        }
        this.clamped = true;
        return defaultValue;
    }

    private String validateFont(Object input, String defaultFont) {
        if (input instanceof String str) {
            if (BackendGlobalVariables.getSystemFonts().contains(str)) return str;
        }
        this.clamped = true;
        return defaultFont;
    }

    // --- GETTERS / SETTERS ---

    public String getLanguage() { return language; }
    public void setLanguage(Object language) {
        this.language = validateString(language, List.of("en", "it"), BackendGlobalVariables.DEFAULT_LANGUAGE);
    }

    public int getCmdColumns() { return cmdColumns; }
    public void setCmdColumns(Object cmdColumns) {
        this.cmdColumns = validateInt(cmdColumns, BackendGlobalVariables.MIN_COLUMNS,
                BackendGlobalVariables.MAX_COLUMNS,
                BackendGlobalVariables.DEFAULT_CMD_COLUMNS);
    }

    public int getOutputLines() { return outputLines; }
    public void setOutputLines(Object outputLines) {
        this.outputLines = validateInt(outputLines, BackendGlobalVariables.MIN_LINES,
                BackendGlobalVariables.MAX_LINES,
                BackendGlobalVariables.DEFAULT_OUTPUT_LINES);
    }

    public int getLogLines() { return logLines; }
    public void setLogLines(Object logLines) {
        this.logLines = validateInt(logLines, BackendGlobalVariables.MIN_LINES,
                BackendGlobalVariables.MAX_LINES,
                BackendGlobalVariables.DEFAULT_LOG_LINES);
    }

    public String getCmdFont() { return cmdFont; }
    public void setCmdFont(Object cmdFont) {
        this.cmdFont = validateFont(cmdFont, BackendGlobalVariables.DEFAULT_CMD_FONT);
    }

    public String getOutputFont() { return outputFont; }
    public void setOutputFont(Object outputFont) {
        this.outputFont = validateFont(outputFont, BackendGlobalVariables.DEFAULT_OUTPUT_FONT);
    }

    public String getLogFont() { return logFont; }
    public void setLogFont(Object logFont) {
        this.logFont = validateFont(logFont, BackendGlobalVariables.DEFAULT_LOG_FONT);
    }

    // --- Metodi per comunicare il clamping al resto dell'app ---

    /**
     * True se durante il caricamento dal file sono stati usati dei valori di default
     * (cioè qualche campo era invalido / fuori range / font non disponibile).
     */
    public boolean wasClamped() {
        return clamped;
    }

    /** Resetta il flag (utile dopo aver notificato l'utente). */
    public void clearClamped() {
        this.clamped = false;
    }

    // --- OPTIONAL: Carica da JSON-like Map ---
    public void loadFromMap(Map<String, Object> json) {
        setLanguage(json.get("language"));
        setCmdColumns(json.get("cmdColumns"));
        setOutputLines(json.get("outputLines"));
        setLogLines(json.get("logLines"));
        setCmdFont(json.get("cmdFont"));
        setOutputFont(json.get("outputFont"));
        setLogFont(json.get("logFont"));
    }
}
