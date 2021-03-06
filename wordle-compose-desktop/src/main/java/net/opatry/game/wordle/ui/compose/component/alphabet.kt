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

package net.opatry.game.wordle.ui.compose.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.ui.compose.theme.keyBg
import net.opatry.game.wordle.ui.compose.theme.keyBgAbsent
import net.opatry.game.wordle.ui.compose.theme.keyBgCorrect
import net.opatry.game.wordle.ui.compose.theme.keyBgPresent
import net.opatry.game.wordle.ui.compose.theme.keyEvaluatedTextColor
import net.opatry.game.wordle.ui.compose.theme.keyTextColor


@Composable
fun Alphabet(alphabet: Map<Char, AnswerFlag>, enabled: Boolean = true, onLetterClick: (Char) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        alphabet.keys.chunked(9).forEach { row ->
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                row.forEach { letter ->
                    AlphabetLetterCell(letter, alphabet[letter]!!, enabled) {
                        onLetterClick(letter)
                    }
                }
            }
        }
    }
}

fun AnswerFlag.keyBackgroundColor(): Color = when (this) {
    AnswerFlag.NONE -> keyBg
    AnswerFlag.PRESENT -> keyBgPresent
    AnswerFlag.ABSENT -> keyBgAbsent
    AnswerFlag.CORRECT -> keyBgCorrect
}

fun AnswerFlag.keyForegroundColor(): Color = when (this) {
    AnswerFlag.NONE -> keyTextColor
    else -> keyEvaluatedTextColor
}

@Composable
fun AlphabetLetterCell(letter: Char, flag: AnswerFlag, enabled: Boolean = true, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(flag.keyBackgroundColor())
    val foregroundColor by animateColorAsState(flag.keyForegroundColor())

    Box(
        Modifier
            .size(width = 36.dp, height = 48.dp)
            .padding(2.dp)
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 2.dp, vertical = 4.dp),
        Alignment.Center,
    ) {
        Text(
            letter.toString(),
            color = foregroundColor,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold
        )
    }
}
