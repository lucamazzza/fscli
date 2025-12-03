package ch.supsi.fscli.frontend.view;

import javafx.scene.control.TextArea;
import lombok.Getter;

import java.util.ResourceBundle;
import java.util.Locale;

@Getter
public class LogAreaView implements View {
    private static final int PREF_LOG_VIEW_ROW_COUNT = 5;

    private final TextArea logView;

    private static LogAreaView instance;

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public static LogAreaView getInstance() {
        if (instance == null) {
            instance = new LogAreaView();
        }
        return instance;
    }

    private LogAreaView() {
        this.logView = new TextArea();
    }

    private void logAreaInit() {
        this.logView.setId("logView");
        this.logView.appendText(MESSAGES.getString("logArea.sampleText") + "\n");

        this.logView.setPrefRowCount(PREF_LOG_VIEW_ROW_COUNT);
        this.logView.setEditable(false);
    }

    @Override
    public void init() {
        logAreaInit();
    }
}
