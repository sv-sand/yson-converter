package ru.svsand.ysonconverter;

import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.svsand.ysonconverter.converter.Converter;
import ru.svsand.ysonconverter.converter.ConverterYsonToCsv;
import ru.svsand.ysonconverter.converter.ConverterYsonToJson;
import ru.svsand.ysonconverter.ui.MainWindow;

import java.io.IOException;
import java.nio.file.Path;

import static java.lang.System.exit;

/**
 * Application launcher that runs after the Spring context is fully initialized.
 */
@Slf4j
@Component
public class Runner implements ApplicationRunner {

    @Getter
    private Config config;

    /**
     * Entry point called by Spring Boot after the application context is ready.
     *
     * @param args application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        config = new Config(args);
        if (config.isHelp())
            printHelp();
        else if (config.isCliMode())
            cliConvert();
        else
            launchUi();
    }

    public static Converter createConverter(Config.Parameters parameters) {
        if (parameters.resultPath().toString().endsWith(".json"))
            return new ConverterYsonToJson(parameters);
        else if (parameters.resultPath().toString().endsWith(".csv"))
            return new ConverterYsonToCsv(parameters);
        else
            throw new RuntimeException("Wrong result path");
    }

    public static Converter createConverter(String sourcePath, String resultPath) {
        Config.Parameters parameters = new Config.Parameters(
                Path.of(sourcePath),
                Path.of(resultPath)
        );

        return createConverter(parameters);
    }

    private void printHelp() {
        System.out.println("""
                Converter YSON files to JSON, CSV
                Help dialog:
                    java -jar yson-converter.jar --help

                Launch in graphic interface mode:
                    java -jar yson-converter.jar

                Usage in cli mode:
                    java -jar yson-converter.jar cli --source=<file.yson> --result=<file>
                Options:
                  --source=<path>   Path to the source .yson file (required)
                  --result=<path>   Path to the output file; must end in .json or .csv (required)
                """);
    }

    private void cliConvert() {
        try {
            Converter converter = createConverter(config.getParameters());
            converter.convert();
        } catch (IOException e) {
            log.error("Failed to convert file", e);
            exit(1);
        }
    }

    private void launchUi() {
        Platform.startup(() -> {
            Stage stage = new Stage();
            new MainWindow(stage);
            stage.show();
        });
    }
}
