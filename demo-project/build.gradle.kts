plugins {
    id("java")
    // This will apply the gradldromus plugin from the parent project
    id("io.github.clojang.gradldromus")
}

// Configure the gradldromus plugin with different settings for different demo runs
gradldromus {
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = false
    maxStackTraceDepth = 5
    showTimings = true
    useColors = true
    passSymbol = "✅"
    failSymbol = "❌"
    skipSymbol = "⏭"
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

// Create different test tasks with different plugin configurations
tasks.register<Test>("demoMinimal") {
    description = "Demo with minimal output (exceptions only)"
    group = "demo"
    
    systemProperty("gradldromus.showExceptions", "true")
    systemProperty("gradldromus.showStackTraces", "false") 
    systemProperty("gradldromus.showFullStackTraces", "false")
    systemProperty("gradldromus.showTimings", "true")
}

tasks.register<Test>("demoStackTraces") {
    description = "Demo with limited stack traces"
    group = "demo"
    
    systemProperty("gradldromus.showExceptions", "true")
    systemProperty("gradldromus.showStackTraces", "true")
    systemProperty("gradldromus.showFullStackTraces", "false")
    systemProperty("gradldromus.maxStackTraceDepth", "5")
    systemProperty("gradldromus.showTimings", "true")
}

tasks.register<Test>("demoFullStackTraces") {
    description = "Demo with full stack traces"
    group = "demo"
    
    systemProperty("gradldromus.showExceptions", "true")
    systemProperty("gradldromus.showStackTraces", "false")
    systemProperty("gradldromus.showFullStackTraces", "true")
    systemProperty("gradldromus.showTimings", "true")
}

tasks.register<Test>("demoNoExceptions") {
    description = "Demo with no exception details (pass/fail only)"
    group = "demo"
    
    systemProperty("gradldromus.showExceptions", "false")
    systemProperty("gradldromus.showStackTraces", "false")
    systemProperty("gradldromus.showFullStackTraces", "false")
    systemProperty("gradldromus.showTimings", "false")
}

tasks.register<Test>("demoCustomSymbols") {
    description = "Demo with custom symbols and colors"
    group = "demo"
    
    systemProperty("gradldromus.passSymbol", "🎉")
    systemProperty("gradldromus.failSymbol", "💥")
    systemProperty("gradldromus.skipSymbol", "🦘")
    systemProperty("gradldromus.showExceptions", "true")
    systemProperty("gradldromus.showStackTraces", "true")
    systemProperty("gradldromus.maxStackTraceDepth", "3")
}

// Convenience task to run all demo variations
tasks.register("demoAll") {
    description = "Run all demo variations"
    group = "demo"
    
    dependsOn(
        "demoMinimal",
        "demoStackTraces", 
        "demoFullStackTraces",
        "demoNoExceptions",
        "demoCustomSymbols"
    )
}
