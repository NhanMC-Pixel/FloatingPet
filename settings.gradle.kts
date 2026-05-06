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
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://github.com/arthenica/ffmpeg-kit/releases/download/v6.0/") }
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
