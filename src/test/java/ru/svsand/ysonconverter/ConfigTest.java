package ru.svsand.ysonconverter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfigTest {

    @Test
    void init_Help() {
        // Arrange
        ApplicationArguments args = mock(ApplicationArguments.class);
        when(args.containsOption("help")).thenReturn(true);

        // Act
        Config config = new Config(args);

        // Assert
        assertThat(config.isHelp()).isTrue();
    }

    @Test
    void init_UiMode() {
        // Arrange
        ApplicationArguments args = mock(ApplicationArguments.class);
        when(args.getNonOptionArgs()).thenReturn(List.of());

        // Act
        Config config = new Config(args);

        // Assert
        assertThat(config.isCliMode()).isFalse();
        assertThat(config.getSettings()).isNull();
    }

    @Test
    void init_cliModeWithJsonResult_setsSourceAndResultPaths() {
        // Arrange
        ApplicationArguments args = mock(ApplicationArguments.class);
        when(args.getNonOptionArgs()).thenReturn(List.of("cli"));
        when(args.getOptionValues("source")).thenReturn(List.of("data.yson"));
        when(args.getOptionValues("result")).thenReturn(List.of("output.json"));

        // Act
        Config config = new Config(args);

        // Assert
        assertThat(config.isCliMode()).isTrue();
        assertThat(config.getSettings().sourcePath()).isEqualTo(Path.of("data.yson"));
        assertThat(config.getSettings().resultPath()).isEqualTo(Path.of("output.json"));
    }

    @Test
    void init_cliModeWithCsvResult_setsSourceAndResultPaths() {
        // Arrange
        ApplicationArguments args = mock(ApplicationArguments.class);
        when(args.getNonOptionArgs()).thenReturn(List.of("cli"));
        when(args.getOptionValues("source")).thenReturn(List.of("data.yson"));
        when(args.getOptionValues("result")).thenReturn(List.of("output.csv"));

        // Act
        Config config = new Config(args);

        // Assert
        assertThat(config.isCliMode()).isTrue();
        assertThat(config.getSettings().sourcePath()).isEqualTo(Path.of("data.yson"));
        assertThat(config.getSettings().resultPath()).isEqualTo(Path.of("output.csv"));
    }
}
