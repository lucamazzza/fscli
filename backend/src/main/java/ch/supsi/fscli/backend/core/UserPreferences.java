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

    public UserPreferences() {}

    public UserPreferences(UserPreferences other) {
        this.language = other.language;
        this.cmdColumns = other.cmdColumns;
        this.outputLines = other.outputLines;
        this.logLines = other.logLines;
        this.cmdFont = other.cmdFont;
        this.outputFont = other.outputFont;
        this.logFont = other.logFont;
    }

    // --- VALIDATOR METHODS ---

    private int validateInt(Object input, int min, int max, int defaultValue) {
        if (input instanceof Number) {
            int value = ((Number) input).intValue();
            if (value >= min && value <= max) return value;
        }
        return defaultValue;
    }

    private String validateString(Object input, List<String> allowed, String defaultValue) {
        if (input instanceof String str) {
            if (allowed.contains(str)) return str;
        }
        return defaultValue;
    }

    private String validateFont(Object input, String defaultFont) {
        if (input instanceof String str) {
            if (BackendGlobalVariables.getSystemFonts().contains(str)) return str;
        }
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
