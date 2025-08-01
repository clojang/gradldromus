# GradlDromus Workarounds

*For Gradle users who want to filter out specific test output messages.*
## Problem
When running tests with Gradle, you may encounter unwanted output messages like:
    
``` 
Gradle Test Run ... PASSED"
```

Those messages are generated by Gradle's core build output system, not by the test framework. This is a known limitation that affects all test output customization plugins.

### Workarounds:

#### 1. Create a Gradle alias
Add this to your `~/.gradle/init.gradle` or project's `gradle.properties`:
```groovy
// In init.gradle
gradle.taskGraph.whenReady { taskGraph ->
    if (taskGraph.hasTask(':test')) {
        gradle.startParameter.setLogLevel(LogLevel.QUIET)
    }
}
```

#### 2. Custom test task
Create a custom task in your build.gradle.kts:
```kotlin
tasks.register("testFiltered") {
    dependsOn("test")
    doFirst {
        gradle.startParameter.logLevel = LogLevel.QUIET
    }
}
```

Then run:
```bash
./gradlew testFiltered
```

#### 3. Filter output with shell
On Unix-like systems:
```bash
./gradlew test | grep -v "Gradle Test Run"
```

## Best Practice
We recommend adding this to your project's README:
```bash
# Run tests with clean output
./gradlew test -q
```
