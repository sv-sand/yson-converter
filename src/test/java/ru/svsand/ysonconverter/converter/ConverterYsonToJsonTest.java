package ru.svsand.ysonconverter.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.svsand.ysonconverter.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConverterYsonToJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void convert_simpleYsonMap_writesValidJsonFile(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path source = tempDir.resolve("input.yson");
        Path result = tempDir.resolve("output.json");
        Files.writeString(source, "{a=1;b=\"hello\";c=%true}");

        Config config = mock(Config.class);
        when(config.getSourcePath()).thenReturn(source);
        when(config.getResultPath()).thenReturn(result);

        ConverterYsonToJson converter = new ConverterYsonToJson(config);

        // Act
        converter.convert();

        // Assert
        assertThat(result).exists();
        JsonNode root = objectMapper.readTree(result.toFile());
        assertThat(root.get("a").asLong()).isEqualTo(1L);
        assertThat(root.get("b").asText()).isEqualTo("hello");
        assertThat(root.get("c").asBoolean()).isTrue();
    }

    @Test
    void convert_numericTypes_preservesIntegerAndDouble(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path source = tempDir.resolve("input.yson");
        Path result = tempDir.resolve("output.json");
        Files.writeString(source, "{count=42;ratio=3.14}");

        Config config = mock(Config.class);
        when(config.getSourcePath()).thenReturn(source);
        when(config.getResultPath()).thenReturn(result);

        ConverterYsonToJson converter = new ConverterYsonToJson(config);

        // Act
        converter.convert();

        // Assert
        JsonNode root = objectMapper.readTree(result.toFile());
        assertThat(root.get("count").asLong()).isEqualTo(42L);
        assertThat(root.get("ratio").asDouble()).isEqualTo(3.14);
    }

    @Test
    void convert_nonexistentSourceFile_throwsIOException(@TempDir Path tempDir) {
        // Arrange
        Path source = tempDir.resolve("nonexistent.yson");
        Path result = tempDir.resolve("output.json");

        Config config = mock(Config.class);
        when(config.getSourcePath()).thenReturn(source);
        when(config.getResultPath()).thenReturn(result);

        ConverterYsonToJson converter = new ConverterYsonToJson(config);

        // Act & Assert
        assertThrows(IOException.class, converter::convert);
    }
}
