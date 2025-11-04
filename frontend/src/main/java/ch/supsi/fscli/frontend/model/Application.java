package ch.supsi.fscli.frontend.model;

import lombok.Getter;

import java.io.InputStream;
import java.util.Properties;

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

        this.name = "fscli";
        this.buildDate = "today";
        this.version = "0.0 - Testing Version";
        this.developers = "XD";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                System.err.println("Could not find properties file: " + resourceName);
                return;
            }
            properties.load(is);
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }

        this.name = properties.getProperty("app.name");
        this.buildDate = properties.getProperty("app.buildDate");
        this.version = properties.getProperty("app.version");
        this.developers = properties.getProperty("app.developers");
    }
}
