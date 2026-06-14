package ru.svsand.ysonconverter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.svsand.ysonconverter.converter.Converter;
import ru.svsand.ysonconverter.converter.ConverterYsonToCsv;
import ru.svsand.ysonconverter.converter.ConverterYsonToJson;

import java.io.IOException;

import static java.lang.System.exit;

/**
 * Application launcher that runs after the Spring context is fully initialized.
 */
@Slf4j
@Component
public class Runner implements ApplicationRunner {

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
            convert();
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

    private void convert() {
        Converter converter;
        if (config.getResultPath().toString().endsWith(".json"))
            converter = new ConverterYsonToJson(config);
        else if (config.getResultPath().toString().endsWith(".csv"))
            converter = new ConverterYsonToCsv(config);
        else
            throw new RuntimeException("Wrong result path");

        try {
            converter.convert();
        } catch (IOException e) {
            log.error("Failed to convert file", e);
            exit(1);
        }
    }
}
