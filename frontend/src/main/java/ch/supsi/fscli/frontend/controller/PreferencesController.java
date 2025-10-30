package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.backend.application.PreferencesService;
import ch.supsi.fscli.frontend.model.PreferencesModel;
import ch.supsi.fscli.frontend.view.PreferencesView;
import javafx.beans.binding.Bindings;

import java.util.List;
import java.util.Map;

public class PreferencesController {

    private final PreferencesModel model;
    private final PreferencesView view;

    public PreferencesController(PreferencesModel model, PreferencesView view) {
        this.model = new PreferencesModel(new PreferencesService());
        this.view = new PreferencesView(sanitizePrefs(model.load()));
        initializeView();
        bindSaveButton();
    }

    public PreferencesController() {
        this(new PreferencesModel(new PreferencesService()), null);
    }

    private void initializeView() {
        Map<String, String> prefs = sanitizePrefs(model.load());


        // inizializza i valori nella view
        view.setLanguage(prefs.get("language"));
        view.setCmdColumns(prefs.get("cmdColumns"));
        view.setOutputLines(prefs.get("outputLines"));
        view.setLogLines(prefs.get("logLines"));
        view.setCmdFont(prefs.get("cmdFont"));
        view.setOutputFont(prefs.get("outputFont"));
        view.setLogFont(prefs.get("logFont"));

        // Bind eventi
        view.setOnSave(this::savePreferences);
        view.setOnCancel(() -> view.close());
        view.setOnReload(this::reloadPreferences);
    }

    private void bindSaveButton() {
        view.saveBtnDisableProperty().bind(
                Bindings.or(view.cmdColumnsInvalidProperty(),
                        Bindings.or(view.outputLinesInvalidProperty(),
                                view.logLinesInvalidProperty()))
        );
    }


    private void savePreferences() {
        model.edit(Map.of(
                "language", view.getLanguage(),
                "cmdColumns", view.getCmdColumns(),
                "outputLines", view.getOutputLines(),
                "logLines", view.getLogLines(),
                "cmdFont", view.getCmdFont(),
                "outputFont", view.getOutputFont(),
                "logFont", view.getLogFont()
        ));
        view.close();
    }

    private void reloadPreferences() {
        model.reload();
        Map<String, String> prefs = sanitizePrefs(model.load());
        view.setLanguage(prefs.get("language"));
        view.setCmdColumns(prefs.get("cmdColumns"));
        view.setOutputLines(prefs.get("outputLines"));
        view.setLogLines(prefs.get("logLines"));
        view.setCmdFont(prefs.get("cmdFont"));
        view.setOutputFont(prefs.get("outputFont"));
        view.setLogFont(prefs.get("logFont"));
    }

    private Map<String, String> sanitizePrefs(Map<String, String> prefs) {
        List<String> allowedLangs = List.of("en", "it", "de", "fr");
        List<String> allowedFonts = ch.supsi.fscli.frontend.util.FrontendGlobalVariables.SYSTEM_FONTS;

        String language = ch.supsi.fscli.frontend.util.FieldValidator.safeLanguage(
                prefs.getOrDefault("language", "en"), allowedLangs, "en");

        String cmdFont = ch.supsi.fscli.frontend.util.FieldValidator.safeFont(
                prefs.getOrDefault("cmdFont", allowedFonts.get(0)), allowedFonts, allowedFonts.get(0));

        String outputFont = ch.supsi.fscli.frontend.util.FieldValidator.safeFont(
                prefs.getOrDefault("outputFont", allowedFonts.get(0)), allowedFonts, allowedFonts.get(0));

        String logFont = ch.supsi.fscli.frontend.util.FieldValidator.safeFont(
                prefs.getOrDefault("logFont", allowedFonts.get(0)), allowedFonts, allowedFonts.get(0));

        int cmdColumns = ch.supsi.fscli.frontend.util.FieldValidator.safeInt(
                prefs.getOrDefault("cmdColumns",
                        String.valueOf(ch.supsi.fscli.frontend.util.FrontendGlobalVariables.DEFAULT_CMD_COLUMNS)),
                ch.supsi.fscli.frontend.util.FrontendGlobalVariables.MIN_COLUMNS,
                ch.supsi.fscli.frontend.util.FrontendGlobalVariables.MAX_COLUMNS,
                ch.supsi.fscli.frontend.util.FrontendGlobalVariables.DEFAULT_CMD_COLUMNS
        );

        int outputLines = ch.supsi.fscli.frontend.util.FieldValidator.safeInt(
                prefs.getOrDefault("outputLines",
                        String.valueOf(ch.supsi.fscli.frontend.util.FrontendGlobalVariables.DEFAULT_OUTPUT_LINES)),
                ch.supsi.fscli.frontend.util.FrontendGlobalVariables.MIN_LINES,
                ch.supsi.fscli.frontend.util.FrontendGlobalVariables.MAX_LINES,
                ch.supsi.fscli.frontend.util.FrontendGlobalVariables.DEFAULT_OUTPUT_LINES
        );

        int logLines = ch.supsi.fscli.frontend.util.FieldValidator.safeInt(
                prefs.getOrDefault("logLines",
                        String.valueOf(ch.supsi.fscli.frontend.util.FrontendGlobalVariables.DEFAULT_OUTPUT_LINES)),
                ch.supsi.fscli.frontend.util.FrontendGlobalVariables.MIN_LINES,
                ch.supsi.fscli.frontend.util.FrontendGlobalVariables.MAX_LINES,
                ch.supsi.fscli.frontend.util.FrontendGlobalVariables.DEFAULT_OUTPUT_LINES
        );

        return Map.of(
                "language", language,
                "cmdFont", cmdFont,
                "outputFont", outputFont,
                "logFont", logFont,
                "cmdColumns", String.valueOf(cmdColumns),
                "outputLines", String.valueOf(outputLines),
                "logLines", String.valueOf(logLines)
        );
    }

    public void show() {
        view.show();
    }
}
