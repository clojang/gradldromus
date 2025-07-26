import org.gradle.api.plugins.quality.CheckstyleExtension
import java.util.Properties

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("signing")
    id("java-gradle-plugin")
    id("checkstyle")
    id("org.sonarqube")
    id("com.github.ben-manes.versions")
    id("org.owasp.dependencycheck")
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "io.github.clojang"
version = "0.3.23"

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

// Generate plugin properties file with merging capability
tasks.register("generatePluginProperties") {
    val outputDir = layout.buildDirectory.dir("generated/resources/main/io/github/clojang/gradldromus")
    val outputFile = outputDir.map { it.file("plugin.properties") }
    val sourcePropertiesFile = file("src/main/resources/io/github/clojang/gradldromus/plugin.properties")
    
    inputs.property("version", project.version)
    if (sourcePropertiesFile.exists()) {
        inputs.file(sourcePropertiesFile)
    }
    outputs.file(outputFile)
    
    doLast {
        val dir = outputDir.get().asFile
        dir.mkdirs()
        val file = outputFile.get().asFile
        
        // Start with base properties
        val properties = Properties()
        
        // Load existing properties if they exist
        if (sourcePropertiesFile.exists()) {
            sourcePropertiesFile.inputStream().use { input ->
                properties.load(input)
            }
        }
        
        // Add/override with generated properties
        properties.setProperty("plugin.name", "GradlDromus")
        properties.setProperty("plugin.version", project.version.toString())
        
        // Write merged properties
        file.outputStream().use { output ->
            properties.store(output, "Generated plugin properties - merged with source")
        }
        
        println("Generated merged plugin.properties at: ${file}")
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

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

// Configure sourcesJar to exclude the original properties file since we're using the merged one
tasks.named<Jar>("sourcesJar") {
    dependsOn("generatePluginProperties")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Configure Nexus publishing for Maven Central (OSSRH Staging API compatibility)
nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            
            // Use unified environment variable names matching GitHub secrets
            username.set(project.findProperty("sonatypeUsername") as String? ?: System.getenv("MAVEN_CENTRAL_USERNAME"))
            password.set(project.findProperty("sonatypePassword") as String? ?: System.getenv("MAVEN_CENTRAL_TOKEN"))
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/clojang/gradldromus")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

// Configure the automatically created plugin publication
afterEvaluate {
    publishing {
        publications {
            // Configure the pluginMaven publication created by java-gradle-plugin
            named<MavenPublication>("pluginMaven") {
                pom {
                    name.set("GradlDromus")
                    description.set("A Gradle plugin for GradlDromus functionality")
                    url.set("https://github.com/clojang/gradldromus")
                    
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("clojang")
                            name.set("Clojang Team")
                            email.set("team@clojang.io")
                        }
                    }
                    
                    scm {
                        connection.set("scm:git:git://github.com/clojang/gradldromus.git")
                        developerConnection.set("scm:git:ssh://github.com:clojang/gradldromus.git")
                        url.set("https://github.com/clojang/gradldromus/tree/main")
                    }
                }
            }
        }
    }
}

// Configure signing
signing {
    val signingKeyId = System.getenv("GPG_KEY_ID")
    val signingKey = System.getenv("GPG_PRIVATE_KEY")
    val signingPassword = System.getenv("GPG_PASSPHRASE")
    
    // Only configure signing if all required properties are present
    if (!signingKeyId.isNullOrEmpty() && !signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        
        // Sign all publications after they're created
        afterEvaluate {
            publishing.publications.forEach { publication ->
                sign(publication)
            }
        }
    } else {
        println("Signing configuration not found - skipping artifact signing")
        println("GPG_KEY_ID present: ${!signingKeyId.isNullOrEmpty()}")
        println("GPG_PRIVATE_KEY present: ${!signingKey.isNullOrEmpty()}")  
        println("GPG_PASSPHRASE present: ${!signingPassword.isNullOrEmpty()}")
    }
}

// Only require signing for non-snapshot versions and when publishing to Maven Central
tasks.withType<Sign>().configureEach {
    onlyIf {
        val signingKeyId = System.getenv("GPG_KEY_ID")
        val signingKey = System.getenv("GPG_PRIVATE_KEY")
        val signingPassword = System.getenv("GPG_PASSPHRASE")
        
        val hasSigningConfig = !signingKeyId.isNullOrEmpty() && !signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()
        println("Signing task ${this.name} - hasSigningConfig: $hasSigningConfig")
        hasSigningConfig
    }
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