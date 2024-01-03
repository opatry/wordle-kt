plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

dependencies {
    implementation(project(":word-data"))
    implementation(project(":game-logic"))
}