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
