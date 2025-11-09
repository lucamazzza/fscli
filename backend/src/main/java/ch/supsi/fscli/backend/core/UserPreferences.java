package ch.supsi.fscli.backend.core;

import ch.supsi.fscli.backend.util.BackendGlobalVariables;

import java.util.List;

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

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private String validateLanguage(String input) {
        List<String> allowed = List.of("en", "it", "de", "fr");
        if (!allowed.contains(input)) {
            return BackendGlobalVariables.DEFAULT_LANGUAGE;
        }
        return input;
    }

    private String validateFont(String input, String defaultFont) {
        if (!BackendGlobalVariables.SYSTEM_FONTS.contains(input)) {
            return defaultFont;
        }
        return input;
    }

    public String getLanguage() { return language; }
    public void setLanguage(String language) {
        this.language = validateLanguage(language);
    }

    public int getCmdColumns() { return cmdColumns; }
    public void setCmdColumns(int cmdColumns) {
        this.cmdColumns = clamp(cmdColumns, BackendGlobalVariables.MIN_COLUMNS, BackendGlobalVariables.MAX_COLUMNS);
    }

    public int getOutputLines() { return outputLines; }
    public void setOutputLines(int outputLines) {
        this.outputLines = clamp(outputLines, BackendGlobalVariables.MIN_LINES, BackendGlobalVariables.MAX_LINES);
    }

    public int getLogLines() { return logLines; }
    public void setLogLines(int logLines) {
        this.logLines = clamp(logLines, BackendGlobalVariables.MIN_LINES, BackendGlobalVariables.MAX_LINES);
    }

    public String getCmdFont() { return cmdFont; }
    public void setCmdFont(String cmdFont) {
        this.cmdFont = validateFont(cmdFont, BackendGlobalVariables.DEFAULT_CMD_FONT);
    }

    public String getOutputFont() { return outputFont; }
    public void setOutputFont(String outputFont) {
        this.outputFont = validateFont(outputFont, BackendGlobalVariables.DEFAULT_OUTPUT_FONT);
    }

    public String getLogFont() { return logFont; }
    public void setLogFont(String logFont) {
        this.logFont = validateFont(logFont, BackendGlobalVariables.DEFAULT_LOG_FONT);
    }
}
