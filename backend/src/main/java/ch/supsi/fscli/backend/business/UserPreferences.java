package ch.supsi.fscli.backend.business;

import ch.supsi.fscli.backend.util.BackendGlobalVariables;

public class UserPreferences {
    private String language = BackendGlobalVariables.DEFAULT_LANGUAGE;
    private int cmdColumns = BackendGlobalVariables.DEFAULT_CMD_COLUMNS;
    private int outputLines = BackendGlobalVariables.DEFAULT_OUTPUT_LINES;
    private int logLines = BackendGlobalVariables.DEFAULT_LOG_LINES;
    private String cmdFont = BackendGlobalVariables.DEFAULT_CMD_FONT;
    private String outputFont = BackendGlobalVariables.DEFAULT_OUTPUT_FONT;
    private String logFont = BackendGlobalVariables.DEFAULT_LOG_FONT;



    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getCmdColumns() {
        return cmdColumns;
    }

    public void setCmdColumns(int cmdColumns) {
        this.cmdColumns = clamp(cmdColumns, BackendGlobalVariables.MIN_COLUMNS, BackendGlobalVariables.MAX_COLUMNS);
    }

    public int getOutputLines() {
        return outputLines;
    }

    public void setOutputLines(int outputLines) {
        this.outputLines = clamp(outputLines, BackendGlobalVariables.MIN_LINES, BackendGlobalVariables.MAX_LINES);
    }

    public int getLogLines() {
        return logLines;
    }

    public void setLogLines(int logLines) {
        this.logLines = clamp(logLines, BackendGlobalVariables.MIN_LINES, BackendGlobalVariables.MAX_LINES);
    }

    public String getCmdFont() {
        return cmdFont;
    }

    public void setCmdFont(String cmdFont) {
        this.cmdFont = cmdFont;
    }

    public String getOutputFont() {
        return outputFont;
    }

    public void setOutputFont(String outputFont) {
        this.outputFont = outputFont;
    }

    public String getLogFont() {
        return logFont;
    }

    public void setLogFont(String logFont) {
        this.logFont = logFont;
    }

    private int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
