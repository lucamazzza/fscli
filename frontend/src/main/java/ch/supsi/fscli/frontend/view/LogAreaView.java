package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.event.CommandLineEvent;
import ch.supsi.fscli.frontend.event.FileSystemEvent;
import ch.supsi.fscli.frontend.listener.Listener;
import ch.supsi.fscli.frontend.util.AppError;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;import javafx.scene.control.TextArea;
import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class LogAreaView implements View {
    private static final int PREF_LOG_VIEW_ROW_COUNT = 5;

    private final TextArea logView;

    private final Listener<FileSystemEvent> fileSystemListener;
    private final Listener<CommandLineEvent> commandLineListener;

    private static LogAreaView instance;

    public static LogAreaView getInstance() {
        if (instance == null) {
            instance = new LogAreaView();
        }
        return instance;
    }

    private LogAreaView() {
        this.logView = new TextArea();
        this.fileSystemListener = event -> {
            if (event == null) return;
            if (event.error() == null) return;
            logError(event.error());
        };
        this.commandLineListener = event -> {
            if (event == null) return;
            if (event.error() == null) return;
            logError(event.error());
        };
    }

    private void logError(AppError error) {
        switch (error) {
            case NEW_SUCCESS, NEW_FAILED_BS_MISSING, SAVE_SUCCESS ,SAVE_FAILED_GENERIC -> log(error);
            case NEW_FAILED_UNSAVED_WORK -> {
                return;
            }
        }
    }

    private void log(AppError error) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formatted = "[" + error.getErrorCode() + "][" + time + "] " + error.getDefaultMessage();
        Platform.runLater(() -> logView.appendText(formatted + "\n"));
    }

    private void logAreaInit() {
        this.logView.setId("logView");
        this.logView.appendText(FrontendMessageProvider.get("logArea.sampleText") + "\n");

        this.logView.setPrefRowCount(PREF_LOG_VIEW_ROW_COUNT);
        this.logView.setEditable(false);
    }

    @Override
    public void init() {
        logAreaInit();
    }
}
