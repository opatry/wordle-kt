pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "Wordle Kotlin"

include("game-logic")
include("word-data")
include("wordle-ascii-cli")
include("wordle-compose-desktop")
include("wordle-compose-android")
include("wordle-compose-mosaic")
