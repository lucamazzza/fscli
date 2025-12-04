package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;import lombok.Getter;

import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Locale;

@Getter
public class Application {
    private String name;
    private String buildDate;
    private String version;
    private String developers;

    private static Application instance;


    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }

    private Application() {
        Properties properties = new Properties();
        String resourceName = "application.properties";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is != null) {
                properties.load(is);
                this.name = properties.getProperty("app.name", this.name);
                this.buildDate = properties.getProperty("app.buildDate", this.buildDate);
                this.version = properties.getProperty("app.version", this.version);
                this.developers = properties.getProperty("app.developers", this.developers);
            }
        } catch (Exception e) {
            System.err.println(FrontendMessageProvider.get("application.errorLoading") + ": " + e);
        }
    }
}
