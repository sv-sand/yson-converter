package ru.svsand.ysonconverter.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.svsand.ysonconverter.Config;
import tech.ytsaurus.ysonjsonconverter.YsonJsonConverter;
import tech.ytsaurus.ysontree.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Spring component that converts YSON files to JSON format.
 */
@Slf4j
public class ConverterYsonToJson implements Converter {

    private final Config config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ConverterYsonToJson(Config config) {
        this.config = config;
    }

    /**
     * Converts the YSON file at the given input path to JSON and writes it to the output path.
     *
     * @throws IOException if reading the input or writing the output fails
     */
    public void convert() throws IOException {
        log.info("Start conversion {} to {}", config.getSourcePath().getFileName(), config.getResultPath().getFileName());

        log.info("Parsing YSON");
        YTreeNode ysonNode = parseYson(config.getSourcePath());

        log.info("Converting YSON to JSON");
        JsonNode jsonNode = YsonJsonConverter.yson2json(JsonNodeFactory.instance, ysonNode);

        log.info("Writing JSON");
        File outputFile = new File(config.getResultPath().toString());
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(outputFile, jsonNode);

        log.info("File {} converted to {}", config.getSourcePath().getFileName(), config.getResultPath().getFileName());
    }

    private YTreeNode parseYson(Path inputPath) throws IOException {
        YTreeNode ysonNode;
        try (InputStream inputStream = Files.newInputStream(inputPath)) {
            ysonNode = YTreeTextSerializer.deserialize(inputStream);
        } catch (IOException e) {
            log.error("Failed to parse YSON", e);
            throw e;
        }
        return ysonNode;
    }
}
