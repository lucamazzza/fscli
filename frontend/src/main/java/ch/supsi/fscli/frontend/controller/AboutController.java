package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.handler.AboutEventHandler;
import ch.supsi.fscli.frontend.model.ApplicationModel;
import lombok.Setter;

public class AboutController implements AboutEventHandler {
    private static AboutController instance;
    @Setter
    private ApplicationModel model;

    public static AboutController getInstance() {
        if (instance == null) {
            instance = new AboutController();
        }
        return instance;
    }

    private AboutController() {
    }


    @Override
    public void showAppInfo() {
        model.getAppInfo();
    }
}
