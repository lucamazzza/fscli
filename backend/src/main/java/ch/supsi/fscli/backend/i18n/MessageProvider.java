package ch.supsi.fscli.backend.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageProvider {

    private static final String BUNDLE_NAME = "messages";
    private Locale locale;

    public MessageProvider() {
        this.locale = Locale.getDefault();
    }

    public MessageProvider(Locale locale) {
        this.locale = locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String get(String key, Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        String pattern = bundle.getString(key);
        return String.format(pattern, args);
    }
}
