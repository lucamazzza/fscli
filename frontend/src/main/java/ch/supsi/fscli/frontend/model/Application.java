package ch.supsi.fscli.frontend.model;

import lombok.Getter;

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

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }

    private Application() {
        Properties properties = new Properties();
        String resourceName = "application.properties";

        this.name = MESSAGES.getString("application.name");
        this.buildDate = MESSAGES.getString("application.buildDate");
        this.version = MESSAGES.getString("application.version");
        this.developers = MESSAGES.getString("application.developers");

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is != null) {
                properties.load(is);
                this.name = properties.getProperty("app.name", this.name);
                this.buildDate = properties.getProperty("app.buildDate", this.buildDate);
                this.version = properties.getProperty("app.version", this.version);
                this.developers = properties.getProperty("app.developers", this.developers);
            }
        } catch (Exception e) {
            System.err.println(MESSAGES.getString("application.errorLoading") + ": " + e);
        }
    }
}
