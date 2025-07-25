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

// Make sure the generated properties are included
sourceSets {
    main {
        resources {
            srcDir("${buildDir}/generated/resources/main")
        }
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