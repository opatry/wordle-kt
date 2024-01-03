/*
 * Copyright (c) 2022 Olivier Patry
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "net.opatry.game.wordle"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.opatry.games.wordle"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = libs.versions.wordleKt.get()

        resourceConfigurations += listOf("en", "fr")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    lint {
        disable += "InvalidVectorPath"
    }

    signingConfigs {
        create("dev") {
            storeFile = file("dev.keystore")
            storePassword = "devdev"
            keyAlias = "dev"
            keyPassword = "devdev"
        }
        create("prod") {
            val keystoreFilePath = findProperty("playstore.keystore.file") as? String
            storeFile = keystoreFilePath?.let(::file)
            storePassword = findProperty("playstore.keystore.password") as? String
            keyAlias = "wordle_compose_android"
            keyPassword = findProperty("playstore.keystore.key_password") as? String
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("dev")
        }
        getByName("release") {
            // we allow dev signing config in release build when not in CI to allow release builds on dev machine
            val ciBuild = (findProperty("ci") as? String)?.toBoolean() ?: false
            signingConfig = if (signingConfigs.getByName("prod").storeFile == null && !ciBuild) {
                signingConfigs.getByName("dev")
            } else {
                signingConfigs.getByName("prod")
            }

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    lint {
        checkDependencies = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.gson)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)

    implementation(libs.bundles.androidx.compose)

    implementation(libs.accompanist.insets)

    implementation(project(":word-data"))
    implementation(project(":game-logic"))
}
