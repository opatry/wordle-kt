plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.mosaic)
    application
}

dependencies {
    implementation(project(":word-data"))
    implementation(project(":game-logic"))
}
