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
    private final Map<String, String> appInfo = new HashMap<>();

    @Setter
    private EventPublisher<AboutEvent> aboutEventPublisher;

    private static ApplicationModel instance;

    public static ApplicationModel getInstance() {
        if (instance == null) {
            instance = new ApplicationModel();
        }
        return instance;
    }

    private ApplicationModel() {
        Properties properties = new Properties();
        String resourceName = "application.properties";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is != null) {
                properties.load(is);
                appInfo.put("AppName", properties.getProperty("app.name"));
                appInfo.put("Build Date", properties.getProperty("app.buildDate"));
                appInfo.put("Version", properties.getProperty("app.version"));
                appInfo.put("Developers", properties.getProperty("app.developers"));
            } else {
                loadDefaults();
            }
        } catch (Exception e) {
            System.err.println(FrontendMessageProvider.get("application.errorLoading") + ": " + e);
            loadDefaults();
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
