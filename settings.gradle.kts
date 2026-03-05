pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Mozilla Maven repository for GeckoView
        maven {
            url = uri("https://maven.mozilla.org/maven2/")
        }
        // Additional Mozilla nightly repo
        maven {
            url = uri("https://nightly.mozilla.org/maven2/")
        }
    }
}
rootProject.name = "KiwiGeckoBrowser"
include(":app")
