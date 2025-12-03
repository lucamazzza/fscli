package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;import javafx.scene.control.TextArea;
import lombok.Getter;

import java.util.ResourceBundle;
import java.util.Locale;

@Getter
public class LogAreaView implements View {
    private static final int PREF_LOG_VIEW_ROW_COUNT = 5;

    private final TextArea logView;

    private static LogAreaView instance;


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
        this.logView.appendText(FrontendMessageProvider.get("logArea.sampleText") + "\n");

        this.logView.setPrefRowCount(PREF_LOG_VIEW_ROW_COUNT);
        this.logView.setEditable(false);
    }

    @Override
    public void init() {
        logAreaInit();
    }
}
