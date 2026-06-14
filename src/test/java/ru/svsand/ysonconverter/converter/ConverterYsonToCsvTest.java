package ru.svsand.ysonconverter.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.svsand.ysonconverter.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConverterYsonToCsvTest {

    @Test
    void convert_singleRow_writesCsvWithHeaderAndRow(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path source = tempDir.resolve("input.yson");
        Path result = tempDir.resolve("output.csv");
        Files.writeString(source, "{score=42;label=\"pass\"}");

        Config config = mock(Config.class);
        when(config.getSettings()).thenReturn(new Config.Settings(source, result));

        ConverterYsonToCsv converter = new ConverterYsonToCsv(config);

        // Act
        converter.convert();

        // Assert
        List<String> lines = Files.readAllLines(result);
        assertThat(lines).hasSize(2);

        List<String> headers = List.of(lines.get(0).split(";"));
        assertThat(headers).containsExactlyInAnyOrder("score", "label");

        int scoreIndex = headers.indexOf("score");
        int labelIndex = headers.indexOf("label");
        String[] row = lines.get(1).split(";");
        assertThat(row[scoreIndex]).isEqualTo("42");
        assertThat(row[labelIndex]).isEqualTo("pass");
    }

    @Test
    void convert_multipleRows_writesCsvWithAllRows(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path source = tempDir.resolve("input.yson");
        Path result = tempDir.resolve("output.csv");
        Files.writeString(source, "{id=1;name=\"Alice\"};{id=2;name=\"Bob\"}");

        Config config = mock(Config.class);
        when(config.getSettings()).thenReturn(new Config.Settings(source, result));

        ConverterYsonToCsv converter = new ConverterYsonToCsv(config);

        // Act
        converter.convert();

        // Assert
        List<String> lines = Files.readAllLines(result);
        assertThat(lines).hasSize(3);

        List<String> headers = List.of(lines.get(0).split(";"));
        assertThat(headers).containsExactlyInAnyOrder("id", "name");

        int idIndex = headers.indexOf("id");
        int nameIndex = headers.indexOf("name");

        String[] row1 = lines.get(1).split(";");
        assertThat(row1[idIndex]).isEqualTo("1");
        assertThat(row1[nameIndex]).isEqualTo("Alice");

        String[] row2 = lines.get(2).split(";");
        assertThat(row2[idIndex]).isEqualTo("2");
        assertThat(row2[nameIndex]).isEqualTo("Bob");
    }

    @Test
    void convert_nonexistentSourceFile_throwsIOException(@TempDir Path tempDir) {
        // Arrange
        Path source = tempDir.resolve("nonexistent.yson");
        Path result = tempDir.resolve("output.csv");

        Config config = mock(Config.class);
        when(config.getSettings()).thenReturn(new Config.Settings(source, result));

        ConverterYsonToCsv converter = new ConverterYsonToCsv(config);

        // Act & Assert
        assertThrows(IOException.class, converter::convert);
    }
}
