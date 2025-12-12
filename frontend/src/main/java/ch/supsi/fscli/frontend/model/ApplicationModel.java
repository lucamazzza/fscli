package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.frontend.event.AboutEvent;
import ch.supsi.fscli.frontend.event.EventPublisher;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import lombok.Setter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Application metadata model.
 * Loads version, build date, and developer information from properties.
 */
public class ApplicationModel {
    @Setter
    private EventPublisher<AboutEvent> aboutEventPublisher;
    private final Map<String, String> appInfo = new HashMap<>();

    private static ApplicationModel instance;

    public static synchronized ApplicationModel getInstance() {
        if (instance == null) {
            Properties properties = new Properties();
            try (InputStream is = ApplicationModel.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (is != null) {
                    properties.load(is);
                }
            } catch (Exception e) {
                System.err.println(FrontendMessageProvider.get("application.errorLoading") + ": " + e);
            }
            instance = new ApplicationModel(properties);
        }
        return instance;
    }

    ApplicationModel(Properties properties) {
        if (properties == null || properties.isEmpty()) {
            loadDefaults();
        } else {
            appInfo.put("AppName", properties.getProperty("app.name", "FSCLI"));
            appInfo.put("Build Date", properties.getProperty("app.buildDate", "UNKNOWN"));
            appInfo.put("Version", properties.getProperty("app.version", "UNKNOWN"));
            appInfo.put("Developers", properties.getProperty("app.developers", "UNKNOWN"));
        }
    }

    private void loadDefaults() {
        appInfo.put("AppName", "FSCLI");
        appInfo.put("Build Date", "UNKNOWN");
        appInfo.put("Version", "UNKNOWN");
        appInfo.put("Developers", "UNKNOWN");
    }

    public void getAppInfo() {
        aboutEventPublisher.notify(new AboutEvent(appInfo));
    }
}
