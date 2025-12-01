package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.event.FileSystemEvent;
import ch.supsi.fscli.frontend.listener.Listener;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class LogAreaView implements View {
    private static final int PREF_LOG_VIEW_ROW_COUNT = 5;

    private final TextArea logView;

    @Setter
    private final Listener<FileSystemEvent> FileSystemListener;

    private static LogAreaView instance;

    public static LogAreaView getInstance() {
        if (instance == null) {
            instance = new LogAreaView();
        }
        return instance;
    }

    private LogAreaView() {
        this.logView = new TextArea();
        this.FileSystemListener = event -> {
            if (event == null) return;
            if (event.successful()) {
                log("Filesystem initialized correctly.");
                return;
            }
            log("Filesystem failed to initialize.");
        };
    }

    private void log(String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formatted = "[" + time + "] " + message;
        Platform.runLater(() -> logView.appendText(formatted + "\n"));
    }

    private void logAreaInit() {
        this.logView.setId("logView");
        this.logView.appendText("This is an example log text...\n");

        this.logView.setPrefRowCount(PREF_LOG_VIEW_ROW_COUNT);
        this.logView.setEditable(false);
    }

    @Override
    public void init() {
        logAreaInit();
    }
}
