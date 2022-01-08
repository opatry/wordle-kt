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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.unit.dp
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.ui.compose.theme.colorAbsent
import net.opatry.game.wordle.ui.compose.theme.colorCorrect
import net.opatry.game.wordle.ui.compose.theme.colorPresent
import net.opatry.game.wordle.ui.compose.theme.darkGray
import net.opatry.game.wordle.ui.compose.theme.keyBg
import net.opatry.game.wordle.ui.compose.theme.keyEvaluatedTextColor
import net.opatry.game.wordle.ui.compose.theme.keyTextColor
import net.opatry.game.wordle.ui.compose.theme.lightGray
import net.opatry.game.wordle.ui.compose.theme.tileTextColor
import net.opatry.game.wordle.ui.compose.theme.white
import org.xml.sax.InputSource

@ExperimentalComposeUiApi
@Composable
fun GameScreen(viewModel: WordleViewModel) {
    val grid by rememberUpdatedState(viewModel.grid)
    val userInput by rememberUpdatedState(viewModel.userInput)
    val answer by rememberUpdatedState(viewModel.answer)

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
        Divider(Modifier.size(0.5.dp), MaterialTheme.colors.onSurface)
        AnswerPlaceHolder(answer, viewModel::restart)
        WordleGrid(grid)
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
    // force 0 without animation when reseting from "xxx" to "" to avoid poor visual
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
        IconButton(onClick = { onRestart() }) {
            Icon(
                loadXmlImageVector(
                    InputSource(ResourceLoader::class.java.getResourceAsStream("/ic_refresh.xml")),
                    density
                ), "Play again"
            )
        }
    }
}

@Composable
fun Alphabet(alphabet: Map<Char, AnswerFlag>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        alphabet.keys.chunked(9).forEach { row ->
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                row.forEach { letter ->
                    AlphabetLetterCell(letter, alphabet[letter]!!)
                }
            }
        }
    }
}


fun AnswerFlag.keyBackgroundColor(): Color = when (this) {
    AnswerFlag.EMPTY -> keyBg
    AnswerFlag.PRESENT -> colorPresent
    AnswerFlag.ABSENT -> colorAbsent
    AnswerFlag.CORRECT -> colorCorrect
}

fun AnswerFlag.keyForegroundColor(): Color = when (this) {
    AnswerFlag.EMPTY -> keyTextColor
    else -> keyEvaluatedTextColor
}

@Composable
fun AlphabetLetterCell(letter: Char, flag: AnswerFlag) {
    // TODO animate color (and state transition?)

    Box(
        Modifier
            .size(width = 36.dp, height = 48.dp)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(flag.keyBackgroundColor())
            .padding(horizontal = 2.dp, vertical = 4.dp),
        Alignment.Center,
    ) {
        Text(
            letter.toString(),
            color = flag.keyForegroundColor(),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun WordleGrid(grid: List<Answer>) {
    Column {
        grid.forEach { row ->
            WordleWordRow(row)
        }
    }
}

@Composable
fun WordleWordRow(row: Answer) {
    Row {
        row.letters.forEachIndexed { index, char ->
            WordleCharCell(char, row.flags[index])
        }
    }
}

fun AnswerFlag.cellColor(): Color = when (this) {
    AnswerFlag.EMPTY -> white
    AnswerFlag.PRESENT -> colorPresent
    AnswerFlag.ABSENT -> colorAbsent
    AnswerFlag.CORRECT -> colorCorrect
}

@Composable
fun WordleCharCell(char: Char, flag: AnswerFlag) {
    val backgroundColor by animateColorAsState(flag.cellColor())
    val (foregroundColor, borderColor) = when (flag) {
        AnswerFlag.EMPTY -> darkGray to lightGray
        else -> tileTextColor to backgroundColor
    }

    Box(
        Modifier
            .size(48.dp)
            .padding(2.dp)
            .border(BorderStroke(1.dp, borderColor))
            .background(backgroundColor),
        Alignment.Center
    ) {
        Text(
            char.toString(),
            color = foregroundColor,
            style = MaterialTheme.typography.body1
        )
    }
}
