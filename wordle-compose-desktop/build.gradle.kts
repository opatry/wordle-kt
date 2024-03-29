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
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    application
}

repositories {
    // Needed to fetch material icons for Desktop
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

group = "net.opatry.game"
version = libs.versions.wordleKt.get()

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing) {
        because("requires Dispatchers.Main & co at runtime for Jvm")
        // java.lang.IllegalStateException: Module with the Main dispatcher is missing. Add dependency providing the Main dispatcher, e.g. "kotlinx-coroutines-android" and ensure it has the same version as "kotlinx-coroutines-core"
        // see also https://github.com/JetBrains/compose-jb/releases/tag/v1.1.1
    }
    implementation(libs.gson)

    // TODO depends on such icons when compatible with up to date compiler & runtime
    //  For now, having this as dependency leads to `Unresolved reference: loadImageBitmap`
    //  If using compiler plugin 1.0.0 (instead of 1.0.1) with Kotlin 1.5.31, still compilation issue
    //  When removed such error, crashes at runtime: java.lang.NoSuchMethodError: "long androidx.compose.ui.unit.DpKt.DpSize-YgX7TsA(float, float)"
//    implementation "androidx.compose.material:material-icons-core-desktop:1.0.0-beta06"
//    implementation "androidx.compose.material:material-icons-extended-desktop:1.0.0-beta06"

    implementation(project(":word-data"))
    implementation(project(":game-logic"))
}

application {
    mainClass = "net.opatry.game.wordle.ui.compose.WordleComposeDesktopKt"
}
