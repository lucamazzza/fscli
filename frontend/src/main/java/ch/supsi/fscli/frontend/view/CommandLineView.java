package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.event.CommandLineEvent;
import ch.supsi.fscli.frontend.event.FileSystemEvent;
import ch.supsi.fscli.frontend.handler.CommandLineEventHandler;
import ch.supsi.fscli.frontend.listener.Listener;
import ch.supsi.fscli.frontend.util.AppError;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CommandLineView implements View {
    private static final int COMMAND_LINE_PREF_COLUMN_COUNT = 72;
    private static final int PREF_OUTPUT_VIEW_ROW_COUNT = 25;

    private final Label commandLineLabel;
    private final Button enter;
    private final TextField commandLine;
    private final TextArea outputView;

    private final Listener<CommandLineEvent> commandLineListener;
    private final Listener<FileSystemEvent> fileSystemListener;

    private String lastCommandExecuted;

    @Setter
    CommandLineEventHandler commandLineEventHandler;

    private static CommandLineView instance;

    public static CommandLineView getInstance() {
        if (instance == null) {
            instance = new CommandLineView();
        }
        return instance;
    }

    private CommandLineView() {
        this.enter = new Button(FrontendMessageProvider.get("commandLine.enter"));
        this.commandLineLabel = new Label(FrontendMessageProvider.get("commandLine.label"));
        this.commandLine = new TextField();
        this.outputView = new TextArea();
        this.commandLineListener = event -> {
            if (event == null) return;
            if (event.error() == null) return;
            if (event.error() == AppError.CMD_EXECUTION_FAILED_FS_MISSING || event.error() == AppError.CMD_EXECUTION_FAILED_BAD_RESPONSE) return;
            String output = event.currentDir() + "$ " + lastCommandExecuted;
            if (event.output() != null && !event.output().isBlank()) {
                output += "\n" + event.output();
            }
            if (event.outputError() != null && !event.outputError().isBlank()) {
                output += "\n" + event.outputError();
            }
            outputView.appendText(output + "\n");
        };
        this.fileSystemListener = event -> {
            if (event == null) return;
            if (event.error() == null) return;
            if (event.error() == AppError.NEW_SUCCESS || event.error() == AppError.LOAD_SUCCESS) {
                outputView.clear();
            }
        };
    }

    /** Carica tutti i testi dal FrontendMessageProvider */
    private void loadTexts() {
        enter.setText(FrontendMessageProvider.get("commandLine.enter"));
        commandLineLabel.setText(FrontendMessageProvider.get("commandLine.label"));
        outputView.clear();
        outputView.appendText(FrontendMessageProvider.get("cli.welcome") + "\n");
        outputView.appendText(FrontendMessageProvider.get("cli.help") + "\n");
        outputView.appendText(FrontendMessageProvider.get("cli.createFileSystem") + "\n\n");
    }

    private void enterButtonInit() {
        this.enter.setId("enter");
        this.enter.setOnAction(e -> executeCommand());
    }

    private void commandLineInit() {
        this.commandLine.setPrefColumnCount(COMMAND_LINE_PREF_COLUMN_COUNT);
        this.commandLine.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                executeCommand();
            }
        });
    }

    private void logAreaInit() {
        this.outputView.setId("outputView");
        this.outputView.appendText(FrontendMessageProvider.get("cli.welcome") + "\n");
        this.outputView.appendText(FrontendMessageProvider.get("cli.help") + "\n");
        this.outputView.appendText(FrontendMessageProvider.get("cli.createFileSystem") + "\n");

        this.outputView.setPrefRowCount(PREF_OUTPUT_VIEW_ROW_COUNT);
        this.outputView.setEditable(false);
        this.outputView.setWrapText(true);
    }
    
    private void executeCommand() {
        String command = commandLine.getText();
        if (command != null && command.trim().equals("clear")) {
            outputView.clear();
            commandLine.clear();
            return;
        }
        if (command == null || command.trim().isEmpty()) return;
        lastCommandExecuted = command;
        commandLineEventHandler.executeCommand(command);
        commandLine.clear();
        outputView.setScrollTop(Double.MAX_VALUE);
    }
    
    public void clearOutput() {
        outputView.clear();
    }
    
    public void appendOutput(String text) {
        outputView.appendText(text);
        outputView.setScrollTop(Double.MAX_VALUE);
    }

    @Override
    public void init() {
        enterButtonInit();
        commandLineInit();
        logAreaInit();
    }
}
