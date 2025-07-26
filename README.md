# GradlDromus

[![Build Status][gh-actions-badge]][gh-actions]

[![Project Logo][logo]][logo-large]

*A beautiful, clean test output formatter for Gradle that replaces the default messy test logging with elegant, readable results.*

## Features

✨ **Clean Terminal Output** - Overwrites Gradle's messy test output with clean, formatted results  
🎨 **Colorized Results** - Color-coded pass/fail/skip indicators with customizable symbols  
📊 **Comprehensive Summary** - Detailed test statistics and timing information  
🔧 **Highly Configurable** - Customize colors, symbols, timings, and terminal width  
🚀 **Zero Configuration** - Works out of the box with sensible defaults  

## Before & After

**Before (Default Gradle):**
```
> Task :test
Gradle Test Run :test STARTED
com.example.MyTest > testSomething() PASSED
com.example.MyTest > testSomethingElse() FAILED
... lots of verbose output ...
```

**After (GradlDromus):**
```
==============================================================================
Running tests with GradlDromus (version: 0.2.0)
------------------------------------------------------------------------------
:modules:common:monitoring:test
    StructuredLoggerTest.shouldLogInfoMessage() ............................💚 (115ms)
    StructuredLoggerTest.shouldLogErrorWithException() .....................💚 (47ms)
    MetricsCollectorTest.shouldCollectMetrics() ............................💚 (45ms)
    MetricsCollectorTest.shouldCollectZeroValue() ..........................💚 (0ms)

Test Summary:
─────────────
Total: 4 tests, 💚 4 passed, 💔 0 failed, 💤 0 skipped
Time: 0.207s

✨ All tests passed!
==============================================================================
```

## Installation

### Using the Gradle Plugin Portal

```kotlin
plugins {
    id("io.github.clojang.gradldromus") version "0.2.0"
}
```

### Using Legacy Plugin Application

```kotlin
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.clojang:gradldromus:0.2.0")
    }
}

apply(plugin = "io.github.clojang.gradldromus")
```

## Configuration

GradlDromus works with zero configuration, but you can customize it to your liking:

```kotlin
gradldromus {
    // Display options
    showModuleNames = true      // Show module/task names
    showMethodNames = true      // Show test method names  
    showTimings = true          // Show execution times
    useColors = true            // Enable colorized output
    
    // Terminal settings
    terminalWidth = 80          // Override terminal width detection
    suppressGradleOutput = false // Suppress Gradle's default test output
    
    // Custom symbols (use your favorites!)
    passSymbol = "💚"           // Pass indicator
    failSymbol = "💔"           // Fail indicator  
    skipSymbol = "💤"           // Skip indicator
}
```

### Symbol Options

Get creative with your test symbols:

```kotlin
gradldromus {
    // Classic
    passSymbol = "✅"
    failSymbol = "❌" 
    skipSymbol = "⏭️"
    
    // Minimal
    passSymbol = "✓"
    failSymbol = "✗"
    skipSymbol = "-"
    
    // Fun
    passSymbol = "🎉"
    failSymbol = "💥"
    skipSymbol = "😴"
}
```

## How It Works

GradlDromus intercepts Gradle's test events and:

1. **Suppresses** Gradle's default verbose test logging
2. **Captures** test results in real-time
3. **Formats** them with clean, readable output
4. **Overwrites** any messy terminal output using smart cursor control
5. **Provides** a comprehensive summary at the end

The result is a clean, professional test output that's easy to scan and understand.

## Advanced Features

### Terminal Width Detection

GradlDromus automatically detects your terminal width for proper formatting:

1. Uses configured `terminalWidth` if set
2. Falls back to `$COLUMNS` environment variable
3. Tries `tput cols` command
4. Defaults to 80 characters

### Multi-Module Support

Works seamlessly with multi-module Gradle projects, showing clear separation between modules:

```
:module-a:test
    TestA.method1() ................................................💚 (23ms)
    TestA.method2() ................................................💚 (15ms)

:module-b:test  
    TestB.method1() ................................................💚 (45ms)
```

### Failure Details

Failed tests show clear error information:

```
    DatabaseTest.shouldConnect() ..................................💔 (156ms)
    → Connection refused: localhost:5432
```

## Requirements

- **Gradle**: 6.0+
- **Java**: 11+

## Contributing

Found a bug or have a feature request? Please open an issue on GitHub!

### Building from Source

```bash
git clone https://github.com/clojang/gradldromus.git
cd gradldromus
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

The plugin uses itself for test output - so you'll see GradlDromus in action while developing!

## License

© 2025, Clojang. All rights reserved.

Licensed under the Apache License, Version 2.0. See `LICENSE` file for details.

## Inspiration

GradlDromus was created because Gradle's default test output is... challenging to read. We wanted something clean, beautiful, and informative - test output you'd actually *want* to look at.

The name combines "Gradle" with "dromus" (from Greek, meaning "running" or "course") - because your tests should run beautifully.

---

**Made with 💚 by developers who care about clean output**

[//]: ---Named-Links---

[logo]: resources/images/logo.jpg
[logo-large]: resources/images/logo-large.jpg
[screenshot]: resources/images/demo-screenshot.png
[gh-actions-badge]: https://github.com/clojang/gradldromus/workflows/CI/badge.svg
[gh-actions]: https://github.com/clojang/gradldromus/actions?query=workflow%3ACI
