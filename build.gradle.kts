import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("java")
    id("org.sonarqube")
    id("com.github.ben-manes.versions")
    id("org.owasp.dependencycheck")
}

group = "io.github.clojang"
version = "0.2.0"

// Configure SonarQube to avoid deprecated behavior
sonar {
    properties {
        property("sonar.gradle.skipCompile", "true")
    }
}

// Make version catalog values available to subprojects via ext properties
ext {
    set("testcontainersBom", libs.testcontainers.bom.get())
    set("junitJupiter", libs.junit.jupiter.get())
    set("mockitoCore", libs.mockito.core.get())
    set("assertjCore", libs.assertj.core.get())
    set("testcontainersCore", libs.testcontainers.core.get())
    set("checkstyleVersion", libs.versions.checkstyle.get())
}

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
            srcDir(layout.buildDirectory.dir("generated/resources/main"))
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
    
    systemProperty("gradldromus.showExceptions", "true")
    systemProperty("gradldromus.showStackTraces", "false") 
    systemProperty("gradldromus.showFullStackTraces", "false")
    systemProperty("gradldromus.showTimings", "true")
}

tasks.register<Test>("demoTestStackTraces") {
    description = "Run demo tests with limited stack traces"
    group = "demo"
    
    testClassesDirs = sourceSets.getByName("demo").output.classesDirs
    classpath = sourceSets.getByName("demo").runtimeClasspath
    
    systemProperty("gradldromus.showExceptions", "true")
    systemProperty("gradldromus.showStackTraces", "true")
    systemProperty("gradldromus.showFullStackTraces", "false")
    systemProperty("gradldromus.maxStackTraceDepth", "5")
    systemProperty("gradldromus.showTimings", "true")
}

tasks.register<Test>("demoTestFullStackTraces") {
    description = "Run demo tests with full stack traces"
    group = "demo"
    
    testClassesDirs = sourceSets.getByName("demo").output.classesDirs
    classpath = sourceSets.getByName("demo").runtimeClasspath
    
    systemProperty("gradldromus.showExceptions", "true")
    systemProperty("gradldromus.showStackTraces", "false")
    systemProperty("gradldromus.showFullStackTraces", "true")
    systemProperty("gradldromus.showTimings", "true")
}

tasks.register<Test>("demoTestNoExceptions") {
    description = "Run demo tests with no exception details (pass/fail only)"
    group = "demo"
    
    testClassesDirs = sourceSets.getByName("demo").output.classesDirs
    classpath = sourceSets.getByName("demo").runtimeClasspath
    
    systemProperty("gradldromus.showExceptions", "false")
    systemProperty("gradldromus.showStackTraces", "false")
    systemProperty("gradldromus.showFullStackTraces", "false")
    systemProperty("gradldromus.showTimings", "false")
}

tasks.register<Test>("demoTestCustomSymbols") {
    description = "Run demo tests with custom symbols and colors"
    group = "demo"
    
    testClassesDirs = sourceSets.getByName("demo").output.classesDirs
    classpath = sourceSets.getByName("demo").runtimeClasspath
    
    systemProperty("gradldromus.passSymbol", "✅")
    systemProperty("gradldromus.failSymbol", "❌")
    systemProperty("gradldromus.skipSymbol", "⏭")
    systemProperty("gradldromus.showExceptions", "true")
    systemProperty("gradldromus.showStackTraces", "true")
    systemProperty("gradldromus.maxStackTraceDepth", "3")
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
tasks.register("generatePluginProperties") {
    val outputDir = layout.buildDirectory.dir("generated/resources/main/io/github/clojang/gradldromus")
    val outputFile = outputDir.map { it.file("plugin.properties") }
    
    inputs.property("version", project.version)
    outputs.file(outputFile)
    
    doLast {
        val dir = outputDir.get().asFile
        dir.mkdirs()
        val file = outputFile.get().asFile
        file.writeText("""
            plugin.name=GradlDromus
            plugin.version=${project.version}
        """.trimIndent())
        println("Generated plugin.properties at: ${file}")
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Handle duplicate resources
tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// Configure OWASP Dependency Check for root project
dependencyCheck {
    // Configuration for OWASP Dependency Check
    formats = listOf("HTML", "JSON", "XML")  // Use string format names
    suppressionFiles = listOf("${rootProject.projectDir}/config/owasp-suppressions.xml")
    analyzers.assemblyEnabled = false

    // NVD API Configuration - try multiple sources
    val apiKey = System.getenv("NVD_API_KEY")
        ?: project.findProperty("nvdApiKey") as String?
        ?: ""

    // Debug print (remove after testing)
    if (apiKey.isNotEmpty()) {
        println("NVD API Key found: ${apiKey.take(4)}...")
    } else {
        println("WARNING: No NVD API Key found!")
    }

    nvd.apiKey = apiKey

    // Clear the old datafeed URL
    nvd.datafeedUrl = ""

    // Retry configuration with increased delays
    nvd.maxRetryCount = 5
    nvd.delay = 10000  // 10 seconds between retries (increased from 5)

    // Fail build on CVSS score 7 or higher
    failBuildOnCVSS = 7.0f

    // IMPORTANT: Scan all subprojects - use project paths as strings
    scanProjects = allprojects.map { it.path }
}
