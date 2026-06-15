package ru.svsand.ysonconverter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SettingsServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void load_fileAbsent_returnsEmpty() {
        // Arrange
        SettingsService service = new SettingsService(tempDir.resolve("settings.cfg"));

        // Act
        Optional<Config.Parameters> result = service.load();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void saveAndLoad_roundTrip_restoresParameters() {
        // Arrange
        Path settingsFile = tempDir.resolve("settings.cfg");
        SettingsService service = new SettingsService(settingsFile);
        Config.Parameters params = new Config.Parameters(
                Path.of("/data/input.yson"),
                Path.of("/data/output.json")
        );

        // Act
        service.save(params);
        Optional<Config.Parameters> loaded = service.load();

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().sourcePath()).isEqualTo(params.sourcePath());
        assertThat(loaded.get().resultPath()).isEqualTo(params.resultPath());
    }

    @Test
    void load_fileWithMissingKeys_returnsEmpty() throws Exception {
        // Arrange
        Path settingsFile = tempDir.resolve("settings.cfg");
        java.nio.file.Files.writeString(settingsFile, "# empty\n");
        SettingsService service = new SettingsService(settingsFile);

        // Act
        Optional<Config.Parameters> result = service.load();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_overwritesPreviousValues() {
        // Arrange
        Path settingsFile = tempDir.resolve("settings.cfg");
        SettingsService service = new SettingsService(settingsFile);
        Config.Parameters first = new Config.Parameters(Path.of("a.yson"), Path.of("a.json"));
        Config.Parameters second = new Config.Parameters(Path.of("b.yson"), Path.of("b.csv"));

        // Act
        service.save(first);
        service.save(second);
        Optional<Config.Parameters> loaded = service.load();

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().sourcePath()).isEqualTo(second.sourcePath());
        assertThat(loaded.get().resultPath()).isEqualTo(second.resultPath());
    }
}
