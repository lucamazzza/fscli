package ch.supsi.fscli.frontend.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Getter;

@Getter
public class CommandLineView implements IView {
    private static final int COMMAND_LINE_PREF_COLUMN_COUNT = 72;

    private final Label commandLineLabel;
    private final Button enter;
    private final TextField commandLine;

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
    }

    private void enterButtonInit() {
        this.enter.setId("enter");
    }

    private void commandLineInit() {
        this.commandLine.setPrefColumnCount(COMMAND_LINE_PREF_COLUMN_COUNT);
    }

    @Override
    public void init() {
        enterButtonInit();
        commandLineInit();
    }

    @Override
    public void update() {

    }
}
