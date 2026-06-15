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
        Optional<SettingsService.Settings> result = service.load();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void saveAndLoad_roundTrip_restoresSettings() {
        // Arrange
        Path settingsFile = tempDir.resolve("settings.cfg");
        SettingsService service = new SettingsService(settingsFile);
        SettingsService.Settings settings = new SettingsService.Settings(
                Path.of("/data/input.yson"),
                Path.of("/data/output.json")
        );

        // Act
        service.save(settings);
        Optional<SettingsService.Settings> loaded = service.load();

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().sourcePath()).isEqualTo(settings.sourcePath());
        assertThat(loaded.get().resultPath()).isEqualTo(settings.resultPath());
    }

    @Test
    void load_fileWithMissingKeys_returnsEmpty() throws Exception {
        // Arrange
        Path settingsFile = tempDir.resolve("settings.cfg");
        java.nio.file.Files.writeString(settingsFile, "# empty\n");
        SettingsService service = new SettingsService(settingsFile);

        // Act
        Optional<SettingsService.Settings> result = service.load();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_overwritesPreviousValues() {
        // Arrange
        Path settingsFile = tempDir.resolve("settings.cfg");
        SettingsService service = new SettingsService(settingsFile);
        SettingsService.Settings first = new SettingsService.Settings(Path.of("a.yson"), Path.of("a.json"));
        SettingsService.Settings second = new SettingsService.Settings(Path.of("b.yson"), Path.of("b.csv"));

        // Act
        service.save(first);
        service.save(second);
        Optional<SettingsService.Settings> loaded = service.load();

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().sourcePath()).isEqualTo(second.sourcePath());
        assertThat(loaded.get().resultPath()).isEqualTo(second.resultPath());
    }
}
