package ch.supsi.fscli.frontend.util;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Locale;

public class FieldValidator {


    public static String validateInt(String input, int min, int max) {
        try {
            int val = Integer.parseInt(input);
            if (val < min || val > max)
                return FrontendMessageProvider.get("validatedField.outOfRange")
                        .replace("{min}", String.valueOf(min))
                        .replace("{max}", String.valueOf(max));
            return null;
        } catch (NumberFormatException ex) {
            return FrontendMessageProvider.get("validatedField.invalidInteger");
        }
    }

    public static int safeInt(String input, int min, int max, int def) {
        try {
            int val = Integer.parseInt(input);
            if (val < min || val > max)
                return def;
            return val;
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    public static String safeLanguage(String input, List<String> allowed, String def) {
        return allowed.contains(input) ? input : def;
    }

    public static String safeFont(String input, List<String> allowed, String def) {
        return allowed.contains(input) ? input : def;
    }
}
