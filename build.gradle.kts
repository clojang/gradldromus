plugins {
    `java-gradle-plugin`
    `maven-publish`
}

group = "io.github.clojang"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    testImplementation("junit:junit:4.13.2")
}

gradlePlugin {
    plugins {
        create("gradldromus") {
            id = "io.github.clojang.gradldromus"
            implementationClass = "io.github.clojang.gradldromus.GradlDromusPlugin"
        }
    }
}

// Configure source sets for demo tests
sourceSets {
    create("demo") {
        java {
            srcDir("src/demo/java")
        }
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }
    main {
        resources {
            srcDir("${buildDir}/generated/resources/main")
        }
    }
}

// Make demo source set extend test configurations
configurations {
    getByName("demoImplementation") {
        extendsFrom(configurations.getByName("testImplementation"))
    }
    getByName("demoRuntimeOnly") {
        extendsFrom(configurations.getByName("testRuntimeOnly"))
    }
}

// Create demo test tasks with different configurations
tasks.register<Test>("demoTestMinimal") {
    description = "Run demo tests with minimal output (exceptions only)"
    group = "demo"
    
    testClassesDirs = sourceSets.getByName("demo").output.classesDirs
    classpath = sourceSets.getByName("demo").runtimeClasspath
    
    // Configure plugin for minimal output
    doFirst {
        val extension = project.extensions.getByType(GradlDromusExtension::class.java)
        extension.showExceptions = true
        extension.showStackTraces = false
        extension.showFullStackTraces = false
        extension.showTimings = true
    }
}

tasks.register<Test>("demoTestStackTraces") {
    description = "Run demo tests with limited stack traces"
    group = "demo"
    
    testClassesDirs = sourceSets.getByName("demo").output.classesDirs
    classpath = sourceSets.getByName("demo").runtimeClasspath
    
    doFirst {
        val extension = project.extensions.getByType(GradlDromusExtension::class.java)
        extension.showExceptions = true
        extension.showStackTraces = true
        extension.showFullStackTraces = false
        extension.maxStackTraceDepth = 5
        extension.showTimings = true
    }
}

tasks.register<Test>("demoTestFullStackTraces") {
    description = "Run demo tests with full stack traces"
    group = "demo"
    
    testClassesDirs = sourceSets.getByName("demo").output.classesDirs
    classpath = sourceSets.getByName("demo").runtimeClasspath
    
    doFirst {
        val extension = project.extensions.getByType(GradlDromusExtension::class.java)
        extension.showExceptions = true
        extension.showStackTraces = false
        extension.showFullStackTraces = true
        extension.showTimings = true
    }
}

tasks.register<Test>("demoTestNoExceptions") {
    description = "Run demo tests with no exception details (pass/fail only)"
    group = "demo"
    
    testClassesDirs = sourceSets.getByName("demo").output.classesDirs
    classpath = sourceSets.getByName("demo").runtimeClasspath
    
    doFirst {
        val extension = project.extensions.getByType(GradlDromusExtension::class.java)
        extension.showExceptions = false
        extension.showStackTraces = false
        extension.showFullStackTraces = false
        extension.showTimings = false
    }
}

tasks.register<Test>("demoTestCustomSymbols") {
    description = "Run demo tests with custom symbols and colors"
    group = "demo"
    
    testClassesDirs = sourceSets.getByName("demo").output.classesDirs
    classpath = sourceSets.getByName("demo").runtimeClasspath
    
    doFirst {
        val extension = project.extensions.getByType(GradlDromusExtension::class.java)
        extension.passSymbol = "✅"
        extension.failSymbol = "❌"  
        extension.skipSymbol = "⏭"
        extension.showExceptions = true
        extension.showStackTraces = true
        extension.maxStackTraceDepth = 3
    }
}

// Convenience task to run all demo variations
tasks.register("demoAll") {
    description = "Run all demo test variations"
    group = "demo"
    
    dependsOn(
        "demoTestMinimal",
        "demoTestStackTraces", 
        "demoTestFullStackTraces",
        "demoTestNoExceptions",
        "demoTestCustomSymbols"
    )
}

// Make demo tests depend on compiling the demo sources
tasks.named("demoTestMinimal") { dependsOn("compileDemoJava") }
tasks.named("demoTestStackTraces") { dependsOn("compileDemoJava") }
tasks.named("demoTestFullStackTraces") { dependsOn("compileDemoJava") }
tasks.named("demoTestNoExceptions") { dependsOn("compileDemoJava") }
tasks.named("demoTestCustomSymbols") { dependsOn("compileDemoJava") }

// More explicit resource processing
tasks.processResources {
    // First, ensure all resources are copied
    from("src/main/resources")
    
    // Then handle the specific properties file with expansion
    filesMatching("**/plugin.properties") {
        expand("version" to project.version)
        println("Processing properties file: $path with version: ${project.version}")
    }
    
    // Debug: print what files are being processed
    doFirst {
        println("ProcessResources task starting...")
        println("Source directories: ${sourceSets.main.get().resources.srcDirs}")
        println("Output directory: ${destinationDir}")
    }
    
    doLast {
        println("ProcessResources task completed")
        // List the files that were actually processed
        fileTree(destinationDir).visit {
            if (!isDirectory) {
                println("Processed: $relativePath")
            }
        }
    }
}

// Alternative approach: create the properties file directly
tasks.create("generatePluginProperties") {
    val outputDir = file("${buildDir}/generated/resources/main/io/github/clojang/gradldromus")
    val outputFile = file("${outputDir}/plugin.properties")
    
    inputs.property("version", project.version)
    outputs.file(outputFile)
    
    doLast {
        outputDir.mkdirs()
        outputFile.writeText("""
            plugin.name=GradlDromus
            plugin.version=${project.version}
        """.trimIndent())
        println("Generated plugin.properties at: ${outputFile}")
    }
}

tasks.processResources {
    dependsOn("generatePluginProperties")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "gradldromus"
            version = project.version.toString()
            
            from(components["java"])
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

// Handle duplicate resources
tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}