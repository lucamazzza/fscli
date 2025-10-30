package ch.supsi.fscli.frontend.view;

import javafx.scene.control.TextArea;
import lombok.Getter;

@Getter
public class CommandLogView implements IView {
    private static final int PREF_OUTPUT_VIEW_ROW_COUNT = 25;

    private final TextArea outputView;

    private static CommandLogView instance;

    public static CommandLogView getInstance() {
        if (instance == null) {
            instance = new CommandLogView();
        }
        return instance;
    }

    private CommandLogView() {
        this.outputView = new TextArea();
    }

    private void logAreaInit() {
        this.outputView.setId("outputView");
        this.outputView.appendText("This is an example output text...\n");

        this.outputView.setPrefRowCount(PREF_OUTPUT_VIEW_ROW_COUNT);
        this.outputView.setEditable(false);
    }

    @Override
    public void init() {
        logAreaInit();
    }

    @Override
    public void update() {

    }
}
