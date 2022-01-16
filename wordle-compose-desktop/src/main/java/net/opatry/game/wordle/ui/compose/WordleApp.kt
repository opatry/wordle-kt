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

package net.opatry.game.wordle.ui.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import net.opatry.game.wordle.data.Settings
import net.opatry.game.wordle.data.WordleRepository
import net.opatry.game.wordle.ui.WordleViewModel
import net.opatry.game.wordle.ui.compose.theme.WordleComposeTheme
import net.opatry.game.wordle.ui.compose.theme.isHighContrastMode
import net.opatry.game.wordle.ui.compose.theme.isSystemInDarkTheme
import net.opatry.game.wordle.words
import java.io.File


private val appDir = File(System.getProperty("user.home"), ".wordle-kt")
private val dataFile = File(appDir, "records.json")
private val settingsFile = File(appDir, "settings.json")

private val settings = Settings(settingsFile)

// FIXME singleton here otherwise recreated at each recomposition, need to be investigated
private val viewModel = WordleViewModel(words, WordleRepository(dataFile))

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun WordleApp() {
    isSystemInDarkTheme = settings.darkMode
    isHighContrastMode = settings.highContrastMode

    WordleComposeTheme {
        Box(
            Modifier.fillMaxSize(),
            Alignment.TopCenter
        ) {
            GameScreen(settings, viewModel)
        }
    }
}
