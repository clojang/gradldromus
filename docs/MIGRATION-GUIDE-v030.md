# GradlDromus v0.3.0 Migration Guide

## Configuration Changes

The configuration API has been simplified and some properties have been removed or renamed between v0.2.0 and v0.3.0.

## v0.2.0 → v0.3.0 Property Changes

| v0.2.0 Property | v0.3.0 Property | Status | Notes |
|-----------------|-----------------|---------|-------|
| `showModuleNames` | ❌ **REMOVED** | Not available | Module names are no longer separately configurable |
| `showMethodNames` | ❌ **REMOVED** | Not available | Method names are always shown |
| `showTimings` | ✅ `showTimings` | **UNCHANGED** | Still available |
| `useColors` | ✅ `useColors` | **UNCHANGED** | Still available |
| `terminalWidth` | 🔄 `lineWidth` | **RENAMED** | Property renamed for clarity |
| `passSymbol` | ✅ `passSymbol` | **UNCHANGED** | Still available |
| `failSymbol` | ✅ `failSymbol` | **UNCHANGED** | Still available |
| `skipSymbol` | ✅ `skipSymbol` | **UNCHANGED** | Still available |
| `showExceptions` | ✅ `showExceptions` | **UNCHANGED** | Still available |
| `showStackTraces` | ✅ `showStackTraces` | **UNCHANGED** | Still available |
| `showFullStackTraces` | ✅ `showFullStackTraces` | **UNCHANGED** | Still available |

## New Properties in v0.3.0

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `maxStackTraceDepth` | `int` | `10` | Maximum depth for stack traces when `showStackTraces` is true |

## Migration Steps

### 1. Update Your Configuration

**v0.2.0 Configuration:**
```kotlin
tasks.withType<Test> {
    gradldromus {
        showModuleNames = true        // ❌ REMOVE - No longer supported
        showMethodNames = true        // ❌ REMOVE - No longer supported  
        showTimings = true           // ✅ KEEP - Still supported
        useColors = true             // ✅ KEEP - Still supported
        terminalWidth = 150          // 🔄 RENAME to lineWidth
        passSymbol = "✅"            // ✅ KEEP - Still supported
        failSymbol = "‼️"            // ✅ KEEP - Still supported
        skipSymbol = "🙈"            // ✅ KEEP - Still supported
        showExceptions = true        // ✅ KEEP - Still supported
        showStackTraces = false      // ✅ KEEP - Still supported
        showFullStackTraces = false  // ✅ KEEP - Still supported
    }
}
```

**v0.3.0 Configuration:**
```kotlin
tasks.withType<Test> {
    gradldromus {
        showTimings = true
        useColors = true
        lineWidth = 150              // 🔄 RENAMED from terminalWidth
        passSymbol = "✅"
        failSymbol = "‼️"
        skipSymbol = "🙈"
        showExceptions = true
        showStackTraces = false
        showFullStackTraces = false
        maxStackTraceDepth = 10      // 🆕 NEW PROPERTY (optional)
    }
}
```

### 2. Alternative: Use Global Configuration

In v0.3.0, you can also configure the plugin globally instead of per-task:

```kotlin
gradldromus {
    showTimings = true
    useColors = true
    lineWidth = 150
    passSymbol = "✅"
    failSymbol = "‼️"
    skipSymbol = "🙈"
    showExceptions = true
    showStackTraces = false
    showFullStackTraces = false
    maxStackTraceDepth = 10
}
```

### 3. System Property Overrides

v0.3.0 supports system property overrides for all configuration options:

```bash
./gradlew test -Dgradldromus.useColors=false -Dgradldromus.lineWidth=100
```

## Behavior Changes

### Removed Features
- **Module names**: No longer displayed separately - simplified output format
- **Method name toggle**: Method names are always shown for clarity

### Improved Features  
- **Stack trace control**: New `maxStackTraceDepth` property for fine-tuned stack trace output
- **Consistent naming**: `lineWidth` is more descriptive than `terminalWidth`
- **Better defaults**: All properties have sensible defaults

## Troubleshooting

### Common Errors

**Error: "Cannot set property 'showModuleNames' on extension"**
- **Cause**: Property removed in v0.3.0
- **Fix**: Remove the `showModuleNames = true` line

**Error: "Cannot set property 'showMethodNames' on extension"**  
- **Cause**: Property removed in v0.3.0
- **Fix**: Remove the `showMethodNames = true` line

**Error: "Cannot set property 'terminalWidth' on extension"**
- **Cause**: Property renamed to `lineWidth`
- **Fix**: Change `terminalWidth = 150` to `lineWidth = 150`

### Validation

After updating your configuration, verify it works:

```bash
./gradlew test --info
```

The plugin should load without errors and display test output according to your settings.
