package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.handler.AboutEventHandler;
import ch.supsi.fscli.frontend.model.Application;

public class AboutController implements AboutEventHandler {
    private static AboutController instance;
    private final Application model = Application.getInstance();

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
        return model.getName();
    }

    @Override
    public String getBuildDate() {
        return model.getBuildDate();
    }

    @Override
    public String getVerion() {
        return model.getVersion();
    }

    @Override
    public String getDevelopers() {
        return model.getDevelopers();
    }
}
