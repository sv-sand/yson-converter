package ru.svsand.ysonconverter.ui;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import ru.svsand.ysonconverter.Runner;
import ru.svsand.ysonconverter.converter.Converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import ru.svsand.ysonconverter.SettingsService;
import ru.svsand.ysonconverter.SettingsService.Settings;

/**
 * Main application window for UI mode.
 * Provides fields for source and result paths, browse buttons, and a Convert button.
 */
@Slf4j
public class MainWindow {

    private static final Path SETTINGS_PATH = Path.of("settings.cfg");

    private final Stage stage;
    private final SettingsService settingsService = new SettingsService(SETTINGS_PATH);
    private final TextField sourcePathField = new TextField();
    private final TextField resultPathField = new TextField();
    private final Button selectSourceButton = new Button("Browse...");
    private final Button selectResultButton = new Button("Browse...");
    private final Button convertButton = new Button("Convert");

    /**
     * Constructs the main window and configures the given stage.
     *
     * @param stage the JavaFX stage to configure
     */
    public MainWindow(Stage stage) {
        this.stage = stage;
        initStage();
        restoreSettings();
    }

    private void initStage() {
        sourcePathField.setPrefWidth(420);
        resultPathField.setPrefWidth(420);
        selectSourceButton.setOnAction(e -> openFileDialog(sourcePathField));
        selectResultButton.setOnAction(e -> saveFileDialog(resultPathField));
        convertButton.setOnAction(e -> convert());

        // Place elements
        GridPane grid = new GridPane(8, 10);
        grid.add(new Label("Source path:"), 0, 0);
        grid.add(sourcePathField, 1, 0);
        grid.add(selectSourceButton, 2, 0);
        grid.add(new Label("Result path:"), 0, 1);
        grid.add(resultPathField, 1, 1);
        grid.add(selectResultButton, 2, 1);

        HBox buttonConvertRow = new HBox(convertButton);
        buttonConvertRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(16, grid, buttonConvertRow);
        root.setPadding(new Insets(16));

        stage.setTitle("YSON Converter");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
    }

    private void openFileDialog(TextField field) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("YSON files (*.yson)", "*.yson"));
        File file = chooser.showOpenDialog(stage);
        if (file != null)
            field.setText(file.getAbsolutePath());
    }

    private void saveFileDialog(TextField field) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"),
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        File file = chooser.showSaveDialog(stage);
        if (file != null)
            field.setText(file.getAbsolutePath());
    }

    private void convert() {
        String sourcePath = sourcePathField.getText().trim();
        String resultPath = resultPathField.getText().trim();

        if (sourcePath.isEmpty()) {
            showError("Please select a source file.");
            return;
        }
        if (resultPath.isEmpty()) {
            showError("Please select a result file.");
            return;
        }

        // Convert in task
        Converter converter = Runner.createConverter(sourcePath, resultPath);
        Task<Void> task = createConversionTask(converter);
        new Thread(task, "converter-thread").start();

        saveSettings();
    }

    private @NonNull Task<Void> createConversionTask(Converter converter) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
                convertButton.setDisable(true);
                converter.convert();
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            convertButton.setDisable(false);
            showInfo("Conversion completed successfully.");
        });
        task.setOnFailed(e -> {
            convertButton.setDisable(false);
            log.error("Conversion failed", task.getException());
            showError("Conversion failed: " + task.getException().getMessage());
        });
        return task;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void saveSettings() {
        settingsService.save(new Settings(
                Path.of(sourcePathField.getText().trim()),
                Path.of(resultPathField.getText().trim())
        ));
    }

    private void restoreSettings() {
        settingsService.load().ifPresent(settings -> {
            sourcePathField.setText(settings.sourcePath().toString());
            resultPathField.setText(settings.resultPath().toString());
        });
    }
}
