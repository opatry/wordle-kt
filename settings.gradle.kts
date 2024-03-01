pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "wordle-kt"

include("game-logic")
include("word-data")
include("wordle-ascii-cli")
include("wordle-compose-app")
include("wordle-compose-android")
include("wordle-compose-mosaic")
