package ru.svsand.ysonconverter;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
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
public class Launcher implements ApplicationRunner {

    private final Config config;

    @Autowired
    public Launcher(Config config) {
        this.config = config;
    }

    /**
     * Entry point called by Spring Boot after the application context is ready.
     *
     * @param args application arguments
     */
    @Override
    public void run(@NonNull ApplicationArguments args) {
        if (config.isCliMode())
            convert();
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
