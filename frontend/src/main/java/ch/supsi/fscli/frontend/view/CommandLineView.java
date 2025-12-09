package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.frontend.event.CommandLineEvent;
import ch.supsi.fscli.frontend.event.FileSystemEvent;
import ch.supsi.fscli.frontend.handler.CommandLineEventHandler;
import ch.supsi.fscli.frontend.listener.Listener;
import ch.supsi.fscli.frontend.util.AppError;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.frontend.model.FileSystemModel;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private int historyIndex = -1;
    private String temporaryCommand = "";

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
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                navigateHistoryUp();
            } else if (event.getCode() == KeyCode.DOWN) {
                event.consume();
                navigateHistoryDown();
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
            resetHistoryNavigation();
            return;
        }
        if (command == null || command.trim().isEmpty()) return;
        lastCommandExecuted = command;
        resetHistoryNavigation();
        FileSystemModel fileSystem = FileSystemModel.getInstance();
        String currentDir = fileSystem.getCurrentDirectory();
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
                outputView.appendText("Error: " + errorMessage + "\n");
            }
        }
        commandLine.clear();
        outputView.setScrollTop(Double.MAX_VALUE);
    }
    
    private void navigateHistoryUp() {
        FileSystemModel fileSystem = FileSystemModel.getInstance();
        if (!fileSystem.isFileSystemReady()) return;
        
        List<String> history = fileSystem.getCommandHistory();
        if (history.isEmpty()) return;
        
        if (historyIndex == -1) {
            temporaryCommand = commandLine.getText();
            historyIndex = history.size() - 1;
        } else if (historyIndex > 0) {
            historyIndex--;
        }
        
        commandLine.setText(history.get(historyIndex));
        commandLine.positionCaret(commandLine.getText().length());
    }
    
    private void navigateHistoryDown() {
        FileSystemModel fileSystem = FileSystemModel.getInstance();
        if (!fileSystem.isFileSystemReady()) return;
        
        List<String> history = fileSystem.getCommandHistory();
        if (history.isEmpty() || historyIndex == -1) return;
        
        if (historyIndex < history.size() - 1) {
            historyIndex++;
            commandLine.setText(history.get(historyIndex));
        } else {
            historyIndex = -1;
            commandLine.setText(temporaryCommand);
        }
        commandLine.positionCaret(commandLine.getText().length());
    }
    
    private void resetHistoryNavigation() {
        historyIndex = -1;
        temporaryCommand = "";
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
