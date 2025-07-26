pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
    plugins {
        // Define plugin versions - these need to be hardcoded in settings.gradle.kts
        // but we can keep them synchronized with the version catalog manually;
        // Unfortunately, this is a limitation of Gradle - the pluginManagement block
        // in settings.gradle.kts executes before the version catalog is available,
        // so we need to have plugin versions defined here in addition to the versions
        // // .toml file.
        id("org.sonarqube") version "4.4.1.3373"
        id("com.github.ben-manes.versions") version "0.52.0"
        id("com.google.cloud.tools.jib") version "3.4.0"
        id("io.spring.dependency-management") version "1.1.7"
        id("org.owasp.dependencycheck") version "12.1.3"
    }
}

rootProject.name = "gradldromus"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven {
            url = uri("https://packages.confluent.io/maven/")
        }
        maven {
            url = uri("https://repo.spring.io/milestone")
        }
    }
}