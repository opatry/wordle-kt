plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    application
}

dependencies {
    implementation(project(":word-data"))
    implementation(project(":game-logic"))
}