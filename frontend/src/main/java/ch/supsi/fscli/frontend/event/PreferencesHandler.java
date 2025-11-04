package ch.supsi.fscli.frontend.event;

import java.util.Map;

public interface PreferencesHandler extends EventHandler {
     void edit(Map<String, String> settings);
     Map<String, String> load(); // at start only
}
