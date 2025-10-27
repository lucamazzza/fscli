package ch.supsi.fscli.frontend.controller;

public interface AboutHandler extends EventHandler {
    public String getAppName();
    public String getBuildDate();
    public String getVerion();
    public String getDevelopers();
}
