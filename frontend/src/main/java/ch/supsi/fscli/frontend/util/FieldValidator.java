package ch.supsi.fscli.frontend.util;

public class FieldValidator {

    public static String validateInt(String input, int min, int max) {
        try {
            int val = Integer.parseInt(input);
            if (val < min || val > max)
                return "Consented value: " + min + " - " + max;
            return null;
        } catch (NumberFormatException ex) {
            return "Insert a valid integer value";
        }
    }
}