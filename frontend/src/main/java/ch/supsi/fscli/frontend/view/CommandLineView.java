package ch.supsi.fscli.frontend.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;

@Getter
public class CommandLineView implements View {
    private static final int COMMAND_LINE_PREF_COLUMN_COUNT = 72;
    private static final int PREF_OUTPUT_VIEW_ROW_COUNT = 25;

    private final Label commandLineLabel;
    private final Button enter;
    private final TextField commandLine;
    private final TextArea outputView;

    private static CommandLineView instance;

    public static CommandLineView getInstance() {
        if (instance == null) {
            instance = new CommandLineView();
        }
        return instance;
    }

    private CommandLineView() {
        this.enter = new Button("enter");
        this.commandLineLabel = new Label("command");
        this.commandLine = new TextField();
        this.outputView = new TextArea();
    }

    private void enterButtonInit() {
        this.enter.setId("enter");
    }

    private void commandLineInit() {
        this.commandLine.setPrefColumnCount(COMMAND_LINE_PREF_COLUMN_COUNT);
    }

    private void logAreaInit() {
        this.outputView.setId("outputView");
        this.outputView.appendText("This is an example output text...\n");

        this.outputView.setPrefRowCount(PREF_OUTPUT_VIEW_ROW_COUNT);
        this.outputView.setEditable(false);
    }

    @Override
    public void init() {
        enterButtonInit();
        commandLineInit();
        logAreaInit();
    }
}
