# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Spring Boot 4.0.6 application (Java 21) that converts YSON format (used in YTsaurus/YQL) to JSON. See the [YSON format docs](https://ytsaurus.tech/docs/en/yql/udf/list/yson).

Group: `ru.svsand`, package root: `ru.svsand.ysonconverter`.

## Coding Guidelines

- Write JavaDoc for all public methods and classes
- Prioritize clean code, readability, efficiency, and maintainability
- Follow the SOLID and KISS principles
- Follow best practise and design patterns appropriate for the language and framework
- Use early returns when possible
- Always add or modify documentation for public methods and classes when creating new functions and classes or modifying existing ones
- Use AAA pattern and write tests with Arrange, Act, Assert structure

## Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "ru.svsand.ysonconverter.ApplicationTests"

# Run a single test method
./gradlew test --tests "ru.svsand.ysonconverter.ApplicationTests.contextLoads"
```
