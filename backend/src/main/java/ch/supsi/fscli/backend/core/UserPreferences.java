package ch.supsi.fscli.backend.core;

public class UserPreferences {
    private String language = "en";
    private int cmdColumns = 80;
    private int outputLines = 10;
    private int logLines = 5;
    private String cmdFont = "Monospaced";
    private String outputFont = "Monospaced";
    private String logFont = "SansSerif";

    private static final int MIN_COLUMNS = 10;
    private static final int MAX_COLUMNS = 100;
    private static final int MIN_LINES = 3;
    private static final int MAX_LINES = 100;

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
        this.cmdColumns = clamp(cmdColumns, MIN_COLUMNS, MAX_COLUMNS);
    }

    public int getOutputLines() {
        return outputLines;
    }

    public void setOutputLines(int outputLines) {
        this.outputLines = clamp(outputLines, MIN_LINES, MAX_LINES);
    }

    public int getLogLines() {
        return logLines;
    }

    public void setLogLines(int logLines) {
        this.logLines = clamp(logLines, MIN_LINES, MAX_LINES);
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
