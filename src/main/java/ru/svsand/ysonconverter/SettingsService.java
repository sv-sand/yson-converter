package ru.svsand.ysonconverter;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * Persists and restores {@link Config.Parameters} to/from a {@code .cfg} file using Java properties format.
 */
@Slf4j
public class SettingsService {

    private static final Path PATH = Path.of("settings.cfg");
    private static final String KEY_SOURCE = "source.path";
    private static final String KEY_RESULT = "result.path";

    /**
     * Loads parameters from the settings file.
     *
     * @return parameters if the file exists and contains valid entries, otherwise empty
     */
    public Optional<Config.Parameters> load() {
        if (!Files.exists(PATH))
            return Optional.empty();

        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(PATH)) {
            props.load(reader);
        } catch (IOException e) {
            log.warn("Failed to read settings file: {}", PATH, e);
            return Optional.empty();
        }

        String source = props.getProperty(KEY_SOURCE, "").trim();
        String result = props.getProperty(KEY_RESULT, "").trim();

        Config.Parameters parameters = new Config.Parameters(Path.of(source), Path.of(result));
        log.info("Settings has been loaded from file: {}", PATH);

        return Optional.of(parameters);
    }

    /**
     * Saves parameters to the settings file, overwriting any existing content.
     *
     * @param parameters the parameters to persist
     */
    public void save(Config.Parameters parameters) {
        Properties props = new Properties();
        props.setProperty(KEY_SOURCE, parameters.sourcePath().toString());
        props.setProperty(KEY_RESULT, parameters.resultPath().toString());

        try (Writer writer = Files.newBufferedWriter(PATH)) {
            props.store(writer, null);
            log.info("Settings has been saved to file: {}", PATH);
        } catch (IOException e) {
            log.warn("Failed to write settings file: {}", PATH, e);
        }
    }
}
