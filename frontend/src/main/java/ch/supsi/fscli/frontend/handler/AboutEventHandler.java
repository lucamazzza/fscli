package ch.supsi.fscli.frontend.handler;

public interface AboutEventHandler extends EventHandler {
    String getAppName();
    String getBuildDate();
    String getVerion();
    String getDevelopers();
}
