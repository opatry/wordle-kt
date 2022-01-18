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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.opatry.game.wordle.Dictionary
import net.opatry.game.wordle.data.Settings
import net.opatry.game.wordle.data.WordleRepository
import net.opatry.game.wordle.loadWords
import net.opatry.game.wordle.ui.WordleViewModel
import net.opatry.game.wordle.ui.compose.component.DictionaryPicker
import net.opatry.game.wordle.ui.compose.theme.WordleComposeTheme
import net.opatry.game.wordle.ui.compose.theme.isHighContrastMode
import net.opatry.game.wordle.ui.compose.theme.isSystemInDarkTheme
import java.io.File

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun WordleApp(settings: Settings, dataFile: File, dictionaries: List<Dictionary>) {
    isSystemInDarkTheme = settings.darkMode
    isHighContrastMode = settings.highContrastMode

    var selectedDictionary by remember {
        mutableStateOf(
            // force choice if no choice
            if (dictionaries.size == 1)
                dictionaries.first()
            else
                null
        )
    }

    WordleComposeTheme {
        // FIXME why nesting 2 Boxes is needed to center 400.dp width content...
        Box(Modifier.fillMaxSize(), Alignment.TopCenter) {
            Box(Modifier.fillMaxHeight().width(400.dp)) {
                when (val dict = selectedDictionary) {
                    null -> {
                        // TODO define a preselected one?
                        //  1. last used if any
                        //  2. find current locale with 5 letters
                        //  3. none
                        DictionaryPicker(dictionaries) { dictionary ->
                            selectedDictionary = dictionary
                        }
                    }
                    else -> {
                        var words by remember { mutableStateOf(emptyList<String>()) }
                        LaunchedEffect(dict) {
                            withContext(Dispatchers.IO) {
                                words = dict.loadWords()
                            }
                        }
                        if (words.isNotEmpty()) {
                            val viewModel = WordleViewModel(words, WordleRepository(dataFile))
                            DisposableEffect(dict) {
                                onDispose {
                                    viewModel.onCleared()
                                }
                            }
                            GameScreen(settings, viewModel)
                        } else {
                            Column(
                                Modifier.fillMaxSize(),
                                Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                                Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Text("Loading words…")
                            }
                        }
                    }
                }
            }
        }
    }
}
