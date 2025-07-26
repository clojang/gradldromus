# GradlDromus Demo Project

This project demonstrates the GradlDromus plugin functionality with intentionally failing tests.

## Usage

### Build the plugin first (from parent directory):
```bash
cd ..
./gradlew publishToMavenLocal
cd demo-project
```

### Run demos:
```bash
# Standard test run (shows plugin output)
./gradlew test

# Different demo variations
./gradlew demoMinimal          # Minimal output (exceptions only)
./gradlew demoStackTraces      # Limited stack traces  
./gradlew demoFullStackTraces  # Full stack traces
./gradlew demoNoExceptions     # Pass/fail only
./gradlew demoCustomSymbols    # Custom symbols and colors

# Run all demo variations
./gradlew demoAll
```

## What These Tests Do

- **ExceptionDemoTests**: Various exception types and failure scenarios
- **SkippedDemoTests**: Demonstrates skipped/ignored test handling
- **TimingDemoTests**: Shows timing display with different durations

All tests are designed to FAIL to showcase the plugin's output formatting capabilities.
