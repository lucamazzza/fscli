package ch.supsi.fscli.frontend.util;

import java.util.List;

public class FieldValidator {

    // --- Validazione numerica (GUI)
    public static String validateInt(String input, int min, int max) {
        try {
            int val = Integer.parseInt(input);
            if (val < min || val > max)
                return "Allowed value: " + min + " - " + max;
            return null;
        } catch (NumberFormatException ex) {
            return "Insert a valid integer value";
        }
    }

    // --- Parsing sicuro di un intero (per file JSON)
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

    // --- Validazione per lingue
    public static String safeLanguage(String input, List<String> allowed, String def) {
        return allowed.contains(input) ? input : def;
    }

    // --- Validazione per font
    public static String safeFont(String input, List<String> allowed, String def) {
        return allowed.contains(input) ? input : def;
    }
}
