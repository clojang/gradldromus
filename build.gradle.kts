import org.gradle.api.plugins.quality.CheckstyleExtension

plugins {
    id("java")
    id("maven-publish")
    id("java-gradle-plugin")
    id("checkstyle")
    id("org.sonarqube")
    id("com.github.ben-manes.versions")
    id("org.owasp.dependencycheck")
}

group = "io.github.clojang"
version = "0.3.0"

// Make version catalog values available via ext properties
ext {
    set("checkstyleVersion", libs.versions.checkstyle.get())
}

// Configure SonarQube to avoid deprecated behavior
sonar {
    properties {
        property("sonar.gradle.skipCompile", "true")
    }
}

// Configure Checkstyle
checkstyle {
    toolVersion = rootProject.ext["checkstyleVersion"] as String
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
    maxErrors = 0
}

dependencies {
    // Only the dependencies actually used
    implementation(gradleApi())
    testImplementation(libs.junit4)
}

gradlePlugin {
    plugins {
        create("gradldromus") {
            id = "io.github.clojang.gradldromus"
            implementationClass = "io.github.clojang.gradldromus.GradlDromusPlugin"
        }
    }
}

// Configure main source set resources
sourceSets {
    main {
        resources {
            srcDir(layout.buildDirectory.dir("generated/resources/main"))
        }  
    }
}

// Generate plugin properties file
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

// Resource processing with property expansion
tasks.processResources {
    dependsOn("generatePluginProperties")
    from("src/main/resources")
    
    filesMatching("**/plugin.properties") {
        expand("version" to project.version)
    }
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

// Configure OWASP Dependency Check
dependencyCheck {
    formats = listOf("HTML", "JSON", "XML")
    suppressionFiles = listOf("${rootProject.projectDir}/config/owasp-suppressions.xml")
    analyzers.assemblyEnabled = false

    val apiKey = System.getenv("NVD_API_KEY")
        ?: project.findProperty("nvdApiKey") as String?
        ?: ""

    if (apiKey.isNotEmpty()) {
        println("NVD API Key found: ${apiKey.take(4)}...")
    } else {
        println("WARNING: No NVD API Key found!")
    }

    nvd.apiKey = apiKey
    nvd.datafeedUrl = ""
    nvd.maxRetryCount = 5
    nvd.delay = 10000

    failBuildOnCVSS = 7.0f
    scanProjects = allprojects.map { it.path }
}