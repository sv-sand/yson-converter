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
 * Persists and restores application settings to/from a {@code .cfg} file using Java properties format.
 */
@Slf4j
public class SettingsService {

    /**
     * Stores the file paths selected by the user in the UI.
     *
     * @param sourcePath path to the YSON source file
     * @param resultPath path to the conversion result file
     */
    public record Settings(
            Path sourcePath,
            Path resultPath
    ) {}

    private static final String KEY_SOURCE = "source.path";
    private static final String KEY_RESULT = "result.path";

    private final Path path;

    /**
     * Constructs a service that reads and writes settings at the given file path.
     *
     * @param path location of the settings file
     */
    public SettingsService(Path path) {
        this.path = path;
    }

    /**
     * Loads settings from the settings file.
     *
     * @return settings if the file exists and contains valid entries, otherwise empty
     */
    public Optional<Settings> load() {
        if (!Files.exists(path))
            return Optional.empty();

        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(path)) {
            props.load(reader);
        } catch (IOException e) {
            log.warn("Failed to read settings file: {}", path, e);
            return Optional.empty();
        }

        String source = props.getProperty(KEY_SOURCE, "").trim();
        String result = props.getProperty(KEY_RESULT, "").trim();

        if (source.isEmpty() || result.isEmpty())
            return Optional.empty();

        Settings settings = new Settings(Path.of(source), Path.of(result));

        log.info("Settings has been loaded from file: {}", path);
        return Optional.of(settings);
    }

    /**
     * Saves settings to the settings file, overwriting any existing content.
     *
     * @param settings the settings to persist
     */
    public void save(Settings settings) {
        Properties props = new Properties();
        props.setProperty(KEY_SOURCE, settings.sourcePath().toString());
        props.setProperty(KEY_RESULT, settings.resultPath().toString());

        try (Writer writer = Files.newBufferedWriter(path)) {
            props.store(writer, null);
            log.info("Settings has been saved to file: {}", path);
        } catch (IOException e) {
            log.warn("Failed to write settings file: {}", path, e);
        }
    }
}
