pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FloatingPet"
include(
    ":app",
    ":core",
    ":data",
    ":domain",
    ":feature_floating",
    ":feature_notifications",
    ":feature_music",
    ":feature_customization",
    ":feature_settings"
)