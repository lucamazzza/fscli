package ch.supsi.fscli.backend.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javafx.scene.text.Font;


public class BackendGlobalVariables {


    //used to get all fonts from the system
    public static final List<String> SYSTEM_FONTS = List.copyOf(Font.getFamilies());

    public static List<String> getSystemFonts() {
        return SYSTEM_FONTS;
    }

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

    //user home path
    public static final Path DEFAULT_PREF_PATH =
            Paths.get(System.getProperty("user.home"), ".fs_prefs.json");

}
