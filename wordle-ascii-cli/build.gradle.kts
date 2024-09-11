plugins {
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    application
}

kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":word-data"))
            implementation(project(":game-logic"))
        }
    }
}