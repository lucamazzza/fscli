package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.frontend.controller.AboutHandler;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationModel implements AbstractModel, AboutHandler {
    private String name;
    private String buildDate;
    private String version;
    private String developers;

    private static ApplicationModel instance;

    public static ApplicationModel getInstance() {
        if (instance == null) {
            instance = new ApplicationModel();
        }
        return instance;
    }

    private ApplicationModel () {
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
            System.err.println("Error: " + e.toString());
        }

        this.name = properties.getProperty("app.name");
        this.buildDate = properties.getProperty("app.buildDate");
        this.version = properties.getProperty("app.version");
        this.developers = properties.getProperty("app.developers");
    }

    @Override
    public String getAppName() {
        return name;
    }

    @Override
    public String getBuildDate() {
        return buildDate;
    }

    @Override
    public String getVerion() {
        return version;
    }

    @Override
    public String getDevelopers() {
        return developers;
    }
}
