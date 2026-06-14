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
import ru.svsand.ysonconverter.Config;
import ru.svsand.ysonconverter.converter.Converter;
import ru.svsand.ysonconverter.converter.ConverterYsonToCsv;
import ru.svsand.ysonconverter.converter.ConverterYsonToJson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Main application window for UI mode.
 * Provides fields for source and result paths, browse buttons, and a Convert button.
 */
@Slf4j
public class MainWindow {

    private final Stage stage;
    private final TextField sourcePathField = new TextField();
    private final TextField resultPathField = new TextField();
    private final Button convertButton = new Button("Convert");

    /**
     * Constructs the main window and configures the given stage.
     *
     * @param stage the JavaFX stage to configure
     */
    public MainWindow(Stage stage) {
        this.stage = stage;
        initUI();
    }

    private void initUI() {
        sourcePathField.setPrefWidth(420);
        resultPathField.setPrefWidth(420);

        Button selectSourceButton = new Button("Browse...");
        selectSourceButton.setOnAction(e -> openFileDialog(sourcePathField));

        Button selectResultButton = new Button("Browse...");
        selectResultButton.setOnAction(e -> saveFileDialog(resultPathField));

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(10);
        grid.add(new Label("Source path:"), 0, 0);
        grid.add(sourcePathField, 1, 0);
        grid.add(selectSourceButton, 2, 0);
        grid.add(new Label("Result path:"), 0, 1);
        grid.add(resultPathField, 1, 1);
        grid.add(selectResultButton, 2, 1);

        convertButton.setOnAction(e -> convert());
        HBox buttonRow = new HBox(convertButton);
        buttonRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(16, grid, buttonRow);
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

        // Create converter
        Config.Settings settings = new Config.Settings(
                Path.of(sourcePath),
                Path.of(resultPath)
        );
        Config config = new Config(settings);
        Converter converter;
        if (resultPath.endsWith(".json"))
            converter = new ConverterYsonToJson(config);
        else if (resultPath.endsWith(".csv"))
            converter = new ConverterYsonToCsv(config);
        else {
            showError("Result file must have a .json or .csv extension.");
            return;
        }

        // Convert in task
        convertButton.setDisable(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
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
        new Thread(task, "converter-thread").start();
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
}
