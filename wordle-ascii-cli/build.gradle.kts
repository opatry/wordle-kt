plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    application
}

dependencies {
    implementation(projects.wordData)
    implementation(projects.gameLogic)
}