package ch.supsi.fscli.frontend.util;

import javafx.scene.text.Font;

import java.util.List;

public class FrontendGlobalVariables {

    private FrontendGlobalVariables(){}

    //used to get all fonts from the system
    public static final List<String> SYSTEM_FONTS = List.copyOf(Font.getFamilies());

    //limits
    public static final int MIN_COLUMNS = 10;
    public static final int MAX_COLUMNS = 100;
    public static final int MIN_LINES = 3;
    public static final int MAX_LINES = 100;

    //default values
    public static final String DEFAULT_LANGUAGE = "en";
    public static final int DEFAULT_CMD_COLUMNS = 80;
    public static final int DEFAULT_OUTPUT_LINES = 10;
    public static final int DEFAULT_LOG_LINES = 5;
    public static final String DEFAULT_CMD_FONT = "Monospaced";
    public static final String DEFAULT_OUTPUT_FONT = "Monospaced";
    public static final String DEFAULT_LOG_FONT = "SansSerif";
    public static final int PREF_INSETS_SIZE = 7;
    public static final int PREF_COMMAND_SPACER_WIDTH = 11;
}
