package ru.svsand.ysonconverter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;

import java.nio.file.Path;
import java.util.List;

import static java.lang.System.exit;

/**
 * Spring configuration that wires application-level beans.
 */
@Slf4j
public class Config {

    @Getter
    private boolean help;

    @Getter
    private boolean cliMode;

    @Getter @Setter
    private Settings settings;

    public record Settings (
            Path sourcePath,
            Path resultPath
    ) {}

    public Config(ApplicationArguments args) {
        parseArgs(args);
    }

    public Config(Settings settings) {
        this.settings = settings;
    }

    private void parseArgs(ApplicationArguments args) {
        help = parseHelp(args);
        cliMode = parseCliMode(args);

        try {
            if (cliMode) {
                settings = new Settings(
                        parseSourcePath(args),
                        parseResultPath(args)
                );
            }
        } catch (IllegalArgumentException e) {
            log.error("Incorrect options", e);
            exit(1);
        }
    }

    private boolean parseHelp(ApplicationArguments args) {
        return args.containsOption("help");
    }

    private boolean parseCliMode(ApplicationArguments args) {
        if (args.getNonOptionArgs().isEmpty())
            return false;

        return args.getNonOptionArgs().getFirst().equals("cli");
    }

    private Path parseSourcePath(ApplicationArguments args) {
        List<String> values = args.getOptionValues("source");
        if (values == null || values.getFirst().isEmpty())
            throw new IllegalArgumentException("Source path is required");

        String value = values.getFirst();
        if (!value.endsWith(".yson"))
            throw new IllegalArgumentException("Source path should have .yson extension");

        return Path.of(value);
    }

    private Path parseResultPath(ApplicationArguments args) {
        List<String> values = args.getOptionValues("result");
        if (values == null || values.getFirst().isEmpty())
            throw new IllegalArgumentException("Result path is required");

        String value = values.getFirst();
        if (!value.endsWith(".json") && !value.endsWith(".csv"))
            throw new IllegalArgumentException("Result path should have .json or .csv extension");

        return Path.of(value);
    }
}
