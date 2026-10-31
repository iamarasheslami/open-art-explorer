// Open Art Explorer - Gradle settings.
//
// Optional library mirror (see docs/DECISIONS.md, D-005). It is OFF by default, so
// contributors and CI use Google Maven and Maven Central directly. To turn it on for
// your own machine only, add this line to your personal Gradle properties file
// (%USERPROFILE%\.gradle\gradle.properties), never to this repository:
//
//   openArtExplorer.useMirror=true

pluginManagement {
    repositories {
        if (providers.gradleProperty("openArtExplorer.useMirror").orNull == "true") {
            maven { url = uri("https://maven.myket.ir/") }
        }
        google {
            content {
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
        if (providers.gradleProperty("openArtExplorer.useMirror").orNull == "true") {
            maven { url = uri("https://maven.myket.ir/") }
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "open-art-explorer"
include(":app")
