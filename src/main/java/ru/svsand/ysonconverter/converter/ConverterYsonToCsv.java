package ru.svsand.ysonconverter.converter;

import lombok.extern.slf4j.Slf4j;
import ru.svsand.ysonconverter.Config;
import tech.ytsaurus.yson.YsonConsumer;
import tech.ytsaurus.yson.YsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converter that transforms YSON list-fragment files into semicolon-delimited CSV.
 */
@Slf4j
public class ConverterYsonToCsv implements Converter {

    private final Config config;
    private final StringBuffer buffer = new StringBuffer();
    private boolean headersWrote = false;

    public ConverterYsonToCsv(Config config) {
        this.config = config;
    }

    @Override
    public void convert() throws IOException {
        log.info("Start conversion {} to {}",
                config.getSettings().sourcePath().getFileName(),
                config.getSettings().resultPath().getFileName()
        );

        headersWrote = false;
        buffer.setLength(0);

        log.info("Parsing YSON");
        try (InputStream inputStream = Files.newInputStream(config.getSettings().sourcePath())) {
            parseYson(inputStream);
        } catch (IOException e) {
            log.error("Failed to parse YSON", e);
            throw e;
        }

        log.info("File {} has been converted to {}",
                config.getSettings().sourcePath().getFileName(),
                config.getSettings().resultPath().getFileName()
        );
    }

    private void parseYson(InputStream inputStream) {
        YsonParser parser = new YsonParser(inputStream);
        YsonConsumer consumer = new YsonTableConsumer() {
            @Override
            public void onEndMap() {
                writeRow(getMap());
                super.onEndMap();
            }
        };
        while (parser.parseListFragmentItem(consumer)) {}
        writeBufferRows();
    }

    private void writeRow(Map<String, Object> row) {
        log.debug("Serialize row {}", row);

        if (!headersWrote)
            writeHeaders(row);

        String string = row.values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(";"));

        buffer.append("\n");
        buffer.append(string);
        if (buffer.length() > 100000)
            writeBufferRows();
    }

    private void writeHeaders(Map<String, Object> row) {
        String headers = row.keySet().stream()
                .map(Object::toString)
                .collect(Collectors.joining(";"));
		try {
			Files.writeString(config.getSettings().resultPath(), headers);
	    } catch (IOException e) {
			throw new RuntimeException(e);
		}

        headersWrote = true;
		log.info("Headers has been wrote: {}", headers);
    }

    private void writeBufferRows() {
		try {
			Files.writeString(config.getSettings().resultPath(), buffer.toString(), StandardOpenOption.APPEND);
	    } catch (IOException e) {
			throw new RuntimeException(e);
		}
		log.info("{} rows has been wrote", buffer.toString().lines().count());
        buffer.setLength(0);
    }
}
