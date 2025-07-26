# Testing GradlDromus Plugin

This document describes the comprehensive test suite for the GradlDromus plugin, including both regular unit tests and specialized demo tests that showcase various failure modes and configuration options.

## Regular Unit Tests

The standard unit tests validate the core functionality of the plugin and run by default.

### Running Regular Tests

```bash
# Run all regular unit tests
./gradlew test

# Run tests with verbose output
./gradlew test --info

# Run tests and generate reports
./gradlew test jacocoTestReport
```

### Test Coverage

- **GradlDromusPluginTest**: Tests plugin application, extension creation, and configuration
- **AnsiColorsTest**: Tests ANSI color handling, with and without color support
- **CleanTerminalPrinterTest**: Tests terminal output functionality and line clearing

## Demo Tests (Failure Mode Showcase)

The demo tests are specifically designed to **fail** in various ways to demonstrate the plugin's exception handling and display capabilities. These tests are separated into their own source set and don't run by default.

### Demo Test Categories

1. **ExceptionDemoTests**: Various exception types and failure scenarios
2. **TimingDemoTests**: Tests with different execution times (mix of pass/fail)
3. **SkippedDemoTests**: Ignored and skipped test scenarios

### Running Demo Tests

#### Basic Demo Commands

```bash
# Run with minimal output (exception messages only)
./gradlew demoTestMinimal

# Run with limited stack traces (5 frames max)
./gradlew demoTestStackTraces

# Run with full stack traces (complete diagnostic info)
./gradlew demoTestFullStackTraces

# Run with no exception details (pass/fail status only)
./gradlew demoTestNoExceptions

# Run with custom symbols and limited stack traces
./gradlew demoTestCustomSymbols

# Run all demo variations sequentially
./gradlew demoAll
```

#### Demo Test Output Examples

**Minimal Output** (`demoTestMinimal`):
```
:demoTestMinimal
src/demo/java/io/github/clojang/gradldromus/demo/ExceptionDemoTests.java

    ExceptionDemoTests.testSimpleAssertionFailure ................. 💔 (15ms)
    → expected:<42> but was:<24>
    
    ExceptionDemoTests.testNullPointerException ................... 💔 (2ms)
    → Cannot invoke "String.length()" because "str" is null
```

**With Stack Traces** (`demoTestStackTraces`):
```
    ExceptionDemoTests.testDeepStackTrace ......................... 💔 (3ms)
    → Exception thrown from deep in the call stack
      java.lang.RuntimeException: Exception thrown from deep in the call stack
        at io.github.clojang.gradldromus.demo.ExceptionDemoTests.methodG(ExceptionDemoTests.java:89)
        at io.github.clojang.gradldromus.demo.ExceptionDemoTests.methodF(ExceptionDemoTests.java:85)
        at io.github.clojang.gradldromus.demo.ExceptionDemoTests.methodE(ExceptionDemoTests.java:81)
        at io.github.clojang.gradldromus.demo.ExceptionDemoTests.methodD(ExceptionDemoTests.java:77)
        at io.github.clojang.gradldromus.demo.ExceptionDemoTests.methodC(ExceptionDemoTests.java:73)
        ... 5 more
```

**No Exception Details** (`demoTestNoExceptions`):
```
    ExceptionDemoTests.testSimpleAssertionFailure ................. 💔
    ExceptionDemoTests.testNullPointerException ................... 💔
    ExceptionDemoTests.testArrayIndexOutOfBounds .................. 💔
```

### Configuring Your Own Tests

You can create your own demo configuration by adding a custom test task:

```kotlin
tasks.register<Test>("myCustomDemo") {
    description = "My custom demo configuration"
    group = "demo"
    
    testClassesDirs = sourceSets.getByName("demo").output.classesDirs
    classpath = sourceSets.getByName("demo").runtimeClasspath
    
    doFirst {
        val extension = project.extensions.getByType(GradlDromusExtension::class.java)
        extension.showExceptions = true
        extension.showStackTraces = true
        extension.maxStackTraceDepth = 8
        extension.passSymbol = "🟢"
        extension.failSymbol = "🔴"
        extension.skipSymbol = "🟡" 
        extension.useColors = true
        extension.showTimings = true
        extension.terminalWidth = 100
    }
}
```

## Configuration Options Tested

The demo tests showcase all available configuration options:

### Exception Display Options
- `showExceptions`: Display exception messages (default: true)
- `showStackTraces`: Display limited stack traces (default: false)  
- `showFullStackTraces`: Display complete stack traces (default: false)
- `maxStackTraceDepth`: Maximum stack trace frames when limited (default: 10)

### Display Options
- `showModuleNames`: Show task/module names (default: true)
- `showMethodNames`: Show test method names (default: true)
- `showTimings`: Show test execution time (default: true)
- `useColors`: Enable ANSI colors (default: true)
- `terminalWidth`: Terminal width for formatting (default: 80)

### Symbols
- `passSymbol`: Symbol for passing tests (default: "💚")
- `failSymbol`: Symbol for failing tests (default: "💔")
- `skipSymbol`: Symbol for skipped tests (default: "💤")

### Advanced Options
- `suppressGradleOutput`: Reduce Gradle's own output (default: false)
- `showStandardStreams`: Show test stdout/stderr (default: false)

## Test Scenarios Covered

### Exception Types Demonstrated
- Simple assertion failures (`assertEquals`, `assertTrue`, etc.)
- Runtime exceptions (`NullPointerException`, `ArrayIndexOutOfBounds`)
- Custom exceptions with and without messages
- Nested exception chains ("Caused by" scenarios)
- Deep stack traces (7+ method calls)
- Multi-line exception messages
- Unicode and special characters in messages

### Timing Scenarios
- Very fast tests (< 10ms)
- Medium duration tests (100-500ms)
- Slow tests (500ms+)
- Computational work simulation
- Variable delays

### Skip/Ignore Scenarios  
- `@Ignore` annotations
- JUnit `Assume` conditions
- Environment-based skipping
- Mixed pass/fail/skip results

## Continuous Integration

For CI environments, consider these configurations:

```bash
# CI-friendly: exceptions only, no colors, suppress Gradle output
./gradlew test -Pgradldromus.useColors=false -Pgradldromus.suppressGradleOutput=true

# Development: full diagnostics
./gradlew demoTestFullStackTraces
```

## Troubleshooting

### Common Issues

1. **Demo tests not found**: Ensure you've compiled the demo sources:
   ```bash
   ./gradlew compileDemoJava
   ```

2. **Colors not showing**: Check terminal support or disable colors:
   ```bash
   ./gradlew demoTest -Pgradldromus.useColors=false
   ```

3. **Output formatting issues**: Adjust terminal width:
   ```bash
   ./gradlew demoTest -Pgradldromus.terminalWidth=120
   ```

### Debug Mode

Enable debug output to see plugin internals:
```bash
./gradlew demoTest --debug
```