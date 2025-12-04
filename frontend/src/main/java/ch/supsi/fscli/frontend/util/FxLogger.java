package ch.supsi.fscli.frontend.util;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FxLogger {

    private static FxLogger instance;
    private TextArea logArea;

    private FxLogger() {}

    public static FxLogger getInstance() {
        if (instance == null) {
            instance = new FxLogger();
        }
        return instance;
    }

    public void setLogArea(TextArea logArea) {
        this.logArea = logArea;
    }

    public void log(String message) {
        if (logArea == null) {
            System.err.println(FrontendMessageProvider.get("logViewNotInitialized") + message);
            return;
        }

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formatted = "[" + time + "] " + message;

        Platform.runLater(() -> logArea.appendText(formatted + "\n"));
    }

    public void setLogAreaRowCount(int rows){
        if(logArea == null){
            Platform.runLater(() -> logArea.setPrefRowCount(rows));
        }
    }
}
