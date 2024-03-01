import kotlinx.coroutines.Dispatchers

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
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kmp)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm("desktop")
    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.gson)

            implementation(projects.wordData)
            implementation(projects.gameLogic)
        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity.compose)

            implementation(libs.bundles.androidx.compose)

            implementation(libs.accompanist.insets)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                // requires Dispatchers.Main & co at runtime for Jvm
                // java.lang.IllegalStateException: Module with the Main dispatcher is missing. Add dependency providing the Main dispatcher, e.g. "kotlinx-coroutines-android" and ensure it has the same version as "kotlinx-coroutines-core"
                // see also https://github.com/JetBrains/compose-jb/releases/tag/v1.1.1
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}

android {
    namespace = "net.opatry.game.wordle.shared"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

repositories {
    // Needed to fetch material icons for Desktop
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

group = "net.opatry.game"
version = libs.versions.wordleKt.get()

// TODO compose.desktop {} configuration
