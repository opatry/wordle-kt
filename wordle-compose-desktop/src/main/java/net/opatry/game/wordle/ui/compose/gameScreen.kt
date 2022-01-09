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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEvent
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.unit.dp
import net.opatry.game.wordle.ui.compose.component.Alphabet
import net.opatry.game.wordle.ui.compose.component.WordleGrid
import org.xml.sax.InputSource

@ExperimentalComposeUiApi
@Composable
fun GameScreen(viewModel: WordleViewModel) {
    val userInput by rememberUpdatedState(viewModel.userInput)

    val focusRequester = FocusRequester()
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // TODO Scaffold?
    Column(
        Modifier
            .focusRequester(focusRequester)
            .focusable(true)
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyUp) {
                    return@onKeyEvent false
                }
                if (event.key == Key.Backspace) {
                    if (userInput.isNotEmpty()) {
                        viewModel.updateUserInput(userInput.dropLast(1))
                    }
                }
                if (event.key == Key.Enter) {
                    viewModel.validateUserInput()
                    true
                } else if (event.key.keyCode in Key.A.keyCode..Key.Z.keyCode) {
                    // FIXME how to do the same without relying on AWT?
                    viewModel.updateUserInput(userInput + event.awtEvent.keyChar)
                    true
                } else {
                    false
                }
            }
            .fillMaxHeight()
            .width(400.dp)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Toolbar()
        Divider()
        AnswerPlaceHolder(viewModel.answer, viewModel::restart)
        WordleGrid(viewModel.grid)
        Alphabet(viewModel.alphabet)
    }
}

@Composable
fun Toolbar() {
    val density = LocalDensity.current
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(
                loadXmlImageVector(
                    InputSource(ResourceLoader::class.java.getResourceAsStream("/ic_help_outline.xml")),
                    density
                ), "How to play"
            )
        }

        Text("Wordle", Modifier.weight(1f, fill = true), style = MaterialTheme.typography.h1)

        IconButton(onClick = {}) {
            Icon(
                loadXmlImageVector(
                    InputSource(ResourceLoader::class.java.getResourceAsStream("/ic_settings_outline.xml")),
                    density
                ), "Settings"
            )
        }
    }
}

@Composable
fun AnswerPlaceHolder(answer: String, onRestart: () -> Unit) {
    val isAnswerVisible = answer.isNotEmpty()
    val animatedAlpha by animateFloatAsState(if (isAnswerVisible) 1f else 0f)
    // force 0 without animation when resetting from "xxx" to "" to avoid poor visual
    val alpha = if (isAnswerVisible) animatedAlpha else 0f

    Row(
        Modifier.alpha(alpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Box(
            Modifier
                .padding(2.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colors.primary)
                .padding(4.dp)
        ) {
            Text(
                answer,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h2
            )
        }

        val density = LocalDensity.current
        IconButton(onClick = onRestart) {
            Icon(
                loadXmlImageVector(
                    InputSource(ResourceLoader::class.java.getResourceAsStream("/ic_refresh.xml")),
                    density
                ), "Play again"
            )
        }
    }
}
