package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.frontend.controller.AboutHandler;

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
        name = "fscli";
        buildDate = "today";
        version = "1.0";
        developers = "Meerte";
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
