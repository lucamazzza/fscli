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
}
