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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.opatry.game.wordle.Dictionary
import net.opatry.game.wordle.data.Settings
import net.opatry.game.wordle.data.WordleRepository
import net.opatry.game.wordle.loadWords
import net.opatry.game.wordle.ui.WordleViewModel
import net.opatry.game.wordle.ui.compose.component.DictionaryPicker
import net.opatry.game.wordle.ui.compose.theme.AppIcon
import net.opatry.game.wordle.ui.compose.theme.WordleComposeTheme
import net.opatry.game.wordle.ui.compose.theme.isHighContrastMode
import net.opatry.game.wordle.ui.compose.theme.isSystemInDarkTheme
import net.opatry.game.wordle.ui.compose.theme.painterResource
import java.io.File
import kotlin.time.ExperimentalTime

sealed class Screen {
    object Intro : Screen()
    data class Loading(val dictionary: Dictionary) : Screen()
    object DictionaryPicker : Screen()
    object NoDictionaryAvailable : Screen()
    data class Game(val dictionary: Dictionary, val words: List<String>) : Screen()
}

@ExperimentalTime
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun WordleApp(settings: Settings, dataFile: File, dictionaries: List<Dictionary>) {
    isSystemInDarkTheme = settings.darkMode
    isHighContrastMode = settings.highContrastMode

    var screenState by remember { mutableStateOf<Screen>(Screen.Intro) }

    WordleComposeTheme {
        // FIXME why nesting 2 Boxes is needed to center 400.dp width content...
        Box(Modifier.fillMaxSize(), Alignment.TopCenter) {
            Box(Modifier.fillMaxHeight().width(400.dp)) {
                // TODO Crossfade transition?
                when (val screen = screenState) {
                    is Screen.Intro -> Intro {
                        screenState = Screen.DictionaryPicker
                    }
                    is Screen.DictionaryPicker -> DictionaryPicker(dictionaries) { dictionary ->
                        screenState = if (dictionary != null) {
                            Screen.Loading(dictionary)
                        } else {
                            Screen.NoDictionaryAvailable
                        }
                    }
                    is Screen.NoDictionaryAvailable -> Text(
                        "No game data available",
                        Modifier.fillMaxWidth().padding(16.dp),
                        color = MaterialTheme.colors.error,
                        textAlign = TextAlign.Center
                    )
                    is Screen.Loading -> WordLoader(screen.dictionary) { words ->
                        screenState = Screen.Game(screen.dictionary, words)
                    }
                    is Screen.Game -> {
                        val viewModel = WordleViewModel(
                            screen.words,
                            screen.dictionary.qualifier == "official",
                            WordleRepository(dataFile)
                        )
                        DisposableEffect(screen.dictionary) {
                            onDispose {
                                viewModel.onCleared()
                            }
                        }
                        GameScreen(settings, viewModel) {
                            screenState = Screen.DictionaryPicker
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun Intro(onDone: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(250)
        visible = true
        delay(750)
        onDone()
    }

    AnimatedVisibility(visible, enter = scaleIn()) {
        Box(
            Modifier.fillMaxSize(),
            Alignment.Center
        ) {
            Image(
                painterResource(AppIcon.Launcher),
                null,
                Modifier.size(192.dp)
            )
        }
    }
}

@Composable
fun WordLoader(dictionary: Dictionary, onLoad: (List<String>) -> Unit) {
    LaunchedEffect(dictionary) {
        withContext(Dispatchers.IO) {
            onLoad(dictionary.loadWords())
        }
    }

    Column(
        Modifier.fillMaxSize(),
        Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text("Loading words…")
    }
}