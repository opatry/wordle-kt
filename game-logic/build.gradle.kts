plugins {
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
}

kotlin {
    jvm()

    sourceSets {
        jvmTest.dependencies {
            implementation(libs.junit4)
        }
    }
}