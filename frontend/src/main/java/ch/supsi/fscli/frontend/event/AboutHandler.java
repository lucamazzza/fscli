package ch.supsi.fscli.frontend.event;

public interface AboutHandler extends EventHandler {
    String getAppName();
    String getBuildDate();
    String getVerion();
    String getDevelopers();
}
