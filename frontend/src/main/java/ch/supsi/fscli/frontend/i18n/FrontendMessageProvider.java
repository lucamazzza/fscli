package ch.supsi.fscli.frontend.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class FrontendMessageProvider {

    private static final String BUNDLE_NAME = "messages_frontend";  // direttamente in resources
    private static Locale currentLocale = Locale.getDefault();

    private FrontendMessageProvider() {}

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
               FrontendMessageProvider.class.getClassLoader()
        );
        String pattern = bundle.getString(key);
        return String.format(pattern, args);
    }
}
