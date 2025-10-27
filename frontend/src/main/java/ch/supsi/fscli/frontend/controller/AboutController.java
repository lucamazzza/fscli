package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.ApplicationModel;

public class AboutController implements AboutHandler {
    private static AboutController instance;

    public static AboutController getInstance() {
        if (instance == null) {
            instance = new AboutController();
        }
        return instance;
    }

    private AboutController() {
    }

    @Override
    public String getAppName() {
        return ApplicationModel.getInstance().getAppName();
    }

    @Override
    public String getBuildDate() {
        return ApplicationModel.getInstance().getBuildDate();
    }

    @Override
    public String getVerion() {
        return ApplicationModel.getInstance().getVerion();
    }

    @Override
    public String getDevelopers() {
        return ApplicationModel.getInstance().getDevelopers();
    }
}
