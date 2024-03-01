import org.gradle.api.tasks.testing.logging.TestExceptionFormat

buildscript {
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.jetbrains.kmp) apply false
    alias(libs.plugins.mosaic) apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

subprojects {
    tasks {
        findByName("test") ?: return@tasks
        named<Test>("test") {
            ignoreFailures = true
            testLogging {
                events("failed", "passed", "skipped")

                exceptionFormat = TestExceptionFormat.SHORT

                debug {
                    exceptionFormat = TestExceptionFormat.FULL
                }
            }
        }
    }
}
