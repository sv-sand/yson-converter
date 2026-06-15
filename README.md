[![Java CI with Gradle](https://github.com/sv-sand/yson-converter/actions/workflows/gradle.yml/badge.svg)](https://github.com/sv-sand/yson-converter/actions/workflows/gradle.yml)
[![Gradle Package](https://github.com/sv-sand/yson-converter/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/sv-sand/yson-converter/actions/workflows/gradle-publish.yml)

# YSON Converter

A command-line tool that converts [YSON](https://ytsaurus.tech/docs/en/yql/udf/list/yson) files (used in YTsaurus/YQL) to **JSON** or **CSV**.

Built with Spring Boot 4.0.6 and Java 21.

## Features

- Convert YSON list-fragment files to pretty-printed JSON
- Convert YSON list-fragment files to semicolon-delimited CSV
- Streams large files efficiently with buffered writes

## Requirements

- Java 21+

## Build

```bash
./gradlew build
```

The JAR is produced at `build/libs/yson-converter-1.0.0.jar`.

## Usage

### Help

```bash
java -jar yson-converter.jar --help
```

### UI mode

Launch the graphical interface by running the JAR with no subcommand:

```bash
java -jar yson-converter.jar
```

A JavaFX window opens with:

- **Source path** — path to the `.yson` file to convert (use **Browse...** to pick one)
- **Result path** — path for the output file; extension determines format (`.json` or `.csv`) (use **Browse...** to pick a location)
- **Convert** — starts the conversion; the button is disabled while conversion runs and a dialog confirms success or failure

The last-used source and result paths are saved to `settings.cfg` in the working directory and restored automatically on the next launch.

### CLI mode

```bash
java -jar yson-converter.jar cli --source=<file.yson> --result=<output-file>
```

| Option | Description |
|--------|-------------|
| `--source=<path>` | Path to the source `.yson` file (required) |
| `--result=<path>` | Path to the output file; must end in `.json` or `.csv` (required) |

**Convert to JSON:**

```bash
java -jar yson-converter.jar cli --source=example/file.yson --result=output.json
```

**Convert to CSV:**

```bash
java -jar yson-converter.jar cli --source=example/file.yson --result=output.csv
```

### Output formats

**JSON** — pretty-printed array of objects:

```json
[ {
  "Name" : "Alice",
  "FamilyName" : "Johnson",
  "Age" : 28,
  "Email" : "alice.johnson@example.com"
}, ... ]
```

**CSV** — semicolon-delimited with a header row derived from the first record's keys:

```
Name;FamilyName;Age;Email
Alice;Johnson;28;alice.johnson@example.com
...
```

## Example

The `example/` directory contains a sample YSON file and its expected JSON output:

```
example/file.yson    # 100 records in YSON list-fragment format
example/output.json  # Converted JSON output
```

## Development

```bash
# Run with Spring Boot dev tools
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "ru.svsand.ysonconverter.ApplicationTests"

# Run a single test method
./gradlew test --tests "ru.svsand.ysonconverter.ApplicationTests.contextLoads"
```

## Dependencies

| Dependency | Purpose |
|-----------|---------|
| `org.springframework.boot:spring-boot-starter` | Application framework |
| `tech.ytsaurus:yson-json-converter:1.2.16` | YSON parsing and YSON→JSON conversion |
| `org.projectlombok:lombok` | Boilerplate reduction |

## Links

- [YSON format documentation](https://ytsaurus.tech/docs/en/yql/udf/list/yson)
- [YTsaurus](https://ytsaurus.tech/)
