package ru.svsand.ysonconverter;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.List;

import static java.lang.System.exit;

/**
 * Spring configuration that wires application-level beans.
 */
@Slf4j
public class Config {

    private final ApplicationArguments args;

    @Getter
    private boolean help;

    @Getter
    private boolean cliMode;

    @Getter
    private Path sourcePath;

    @Getter
    private Path resultPath;

    public Config(ApplicationArguments args) {
        this.args = args;
        init();
    }

    private void init() {
        help = args.containsOption("help");
        cliMode = getCliModeOption();
        try {
            if (cliMode)
                parseCliArgs();
        } catch (IllegalArgumentException e) {
            log.error("Incorrect options", e);
            exit(1);
        }
    }

    private boolean getCliModeOption() {
        if (args.getNonOptionArgs().isEmpty())
            return false;

        return args.getNonOptionArgs().getFirst().equals("cli");
    }

    private void parseCliArgs() throws IllegalArgumentException {
        String sourceValue = getCliOption("source");
        if (sourceValue.isEmpty())
            throw new IllegalArgumentException("Source path is required");
        else if (!sourceValue.endsWith(".yson"))
            throw new IllegalArgumentException("Source path should have .yson extension");
        else
            sourcePath = Path.of(sourceValue);

        String resultValue = getCliOption("result");
        if (resultValue.isEmpty())
            throw new IllegalArgumentException("Result path is required");
        else if (!resultValue.endsWith(".json") && !resultValue.endsWith(".csv"))
            throw new IllegalArgumentException("Result path should have .json or .csv extension");
        else
            resultPath = Path.of(resultValue);
    }

    private String getCliOption(String name) {
        String defaultValue = "";

        List<String> values = args.getOptionValues(name);
        if (values == null)
            return defaultValue;

        return values.getFirst();
    }
}
