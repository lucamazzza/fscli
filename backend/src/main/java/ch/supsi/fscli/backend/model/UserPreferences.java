package ch.supsi.fscli.backend.model;

public class UserPreferences {
    private String language = "en";
    private int cmdColumns = 80;
    private int outputLines = 10;
    private int logLines = 5;
    private String cmdFont = "Monospaced";
    private String outputFont = "Monospaced";
    private String logFont = "SansSerif";

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
        this.cmdColumns = cmdColumns;
    }

    public int getOutputLines() {
        return outputLines;
    }

    public void setOutputLines(int outputLines) {
        this.outputLines = outputLines;
    }

    public int getLogLines() {
        return logLines;
    }

    public void setLogLines(int logLines) {
        this.logLines = logLines;
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
}
