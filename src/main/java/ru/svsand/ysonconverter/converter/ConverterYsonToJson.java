package ru.svsand.ysonconverter.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.extern.slf4j.Slf4j;
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

    private final Config.Parameters parameters;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConverterYsonToJson(Config.Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Converts the YSON file
     *
     * @throws IOException if reading the input or writing the output fails
     */
    @Override
    public void convert() throws IOException {
        log.info("Start conversion {} to {}",
                parameters.sourcePath().getFileName(),
                parameters.resultPath().getFileName()
        );

        log.info("Parsing YSON");
        YTreeNode ysonNode = parseYson(parameters.sourcePath());

        log.info("Converting YSON to JSON");
        JsonNode jsonNode = YsonJsonConverter.yson2json(JsonNodeFactory.instance, ysonNode);

        log.info("Writing JSON");
        File outputFile = new File(parameters.resultPath().toString());
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(outputFile, jsonNode);

        log.info("File {} converted to {}",
                parameters.sourcePath().getFileName(),
                parameters.resultPath().getFileName()
        );
    }

    private YTreeNode parseYson(Path inputPath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(inputPath)) {
            return YTreeTextSerializer.deserialize(inputStream);
        }
    }
}
