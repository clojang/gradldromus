# GradlDromus Demo Project

This project demonstrates the GradlDromus plugin functionality with both failing and passing tests.

## Usage

### Build the plugin first (from parent directory):
```bash
cd ..
./gradlew publishToMavenLocal
cd demo-project
```

### Run demos:
```bash
# Standard test run (shows plugin output for all tests - mostly failing)
./gradlew test

# Different demo variations
./gradlew demoMinimal          # Minimal output (exceptions only)
./gradlew demoStackTraces      # Limited stack traces  
./gradlew demoFullStackTraces  # Full stack traces
./gradlew demoNoExceptions     # Pass/fail only
./gradlew demoCustomSymbols    # Custom symbols and colors
./gradlew demoPassing          # All passing tests only

# Run all demo variations
./gradlew demoAll
```

## What These Tests Do

### Failing Tests (Default)
- **ExceptionDemoTests**: Various exception types and failure scenarios
- **SkippedDemoTests**: Demonstrates skipped/ignored test handling
- **TimingDemoTests**: Shows timing display with different durations

Most tests are designed to FAIL to showcase the plugin's output formatting capabilities.

### Passing Tests
- **BasicMathTests**: Mathematical operations and calculations
- **StringOperationsTests**: String manipulation and validation
- **CollectionOperationsTests**: Collection operations and data structures

All tests in the `passing` package are designed to PASS to demonstrate successful test output formatting.

### Running Only Passing Tests
```bash
# To see how the plugin displays successful tests:
./gradlew demoPassing

# Or run passing tests with different configurations:
./gradlew demoPassing -Dgradldromus.passSymbol="🎉" -Dgradldromus.showTimings=true
```

### Running Only Failing Tests
```bash
# To exclude passing tests and only see failures:
./gradlew test --tests "io.github.clojang.gradldromus.demo.*" --exclude-tests "**/passing/**"
```
