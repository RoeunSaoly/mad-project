pluginManagement {
    repositories {
        google {content {
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
            includeGroupByRegex("androidx.*")
        }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    // The explicit versionCatalogs block has been removed.
    // Gradle will automatically load "gradle/libs.versions.toml" by convention.
}

rootProject.name = "mad-project"
include(":app")
