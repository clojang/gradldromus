pluginManagement {
    // Include the parent project's build output so we can use the plugin
    includeBuild("../")
    
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "gradldromus-demo"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}
