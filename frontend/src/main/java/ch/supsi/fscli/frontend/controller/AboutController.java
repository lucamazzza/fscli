package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.event.AboutHandler;
import ch.supsi.fscli.frontend.model.Application;

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
        return Application.getInstance().getName();
    }

    @Override
    public String getBuildDate() {
        return Application.getInstance().getBuildDate();
    }

    @Override
    public String getVerion() {
        return Application.getInstance().getVersion();
    }

    @Override
    public String getDevelopers() {
        return Application.getInstance().getDevelopers();
    }
}
