package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;import ch.supsi.fscli.frontend.model.FileSystem;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

    // Costruttore: solo crea componenti, senza testi
    private CommandLineView() {
        this.enter = new Button();
        this.commandLineLabel = new Label();
        this.commandLine = new TextField();
        this.outputView = new TextArea();
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

        FileSystem fileSystem = FileSystem.getInstance();
        String currentDir = fileSystem.getCurrentDirectory();
        outputView.appendText(currentDir + " $ " + command + "\n");

        if (fileSystem.isFileSystemReady() && command.trim().equals("help")) {
            for (String s : fileSystem.getAllCommandsHelp()) outputView.appendText(s + "\n");
            commandLine.clear();
            return;
        }

        CommandResponseDTO response = fileSystem.executeCommand(command);
        if (response.isSuccess()) {
            if (response.getOutput() != null && !response.getOutput().isEmpty()) {
                for (String line : response.getOutput()) {
                    outputView.appendText(line + "\n");
                }
            }
        } else {
            String errorMessage = response.getErrorMessage();
            if (errorMessage != null && !errorMessage.isEmpty()) {
                outputView.appendText(FrontendMessageProvider.get("cli.error") + ": " + errorMessage + "\n");
            }
        }

        outputView.appendText("\n");
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
        loadTexts();       // carica i testi dalla lingua corrente
        enterButtonInit();
        commandLineInit();
        logAreaInit();
    }
}
