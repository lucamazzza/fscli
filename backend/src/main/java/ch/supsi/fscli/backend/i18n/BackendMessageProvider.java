package ch.supsi.fscli.backend.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class BackendMessageProvider {

    private static final String BUNDLE_NAME = "messages_backend";
    private static Locale currentLocale = Locale.getDefault();

    private BackendMessageProvider() {}

    public static void setLocale(Locale locale) {
        currentLocale = locale;
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    public static String get(String key, Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle(
                BUNDLE_NAME,
                currentLocale,
                BackendMessageProvider.class.getClassLoader()
        );
        String pattern = bundle.getString(key);
        return String.format(pattern, args);
    }
}
