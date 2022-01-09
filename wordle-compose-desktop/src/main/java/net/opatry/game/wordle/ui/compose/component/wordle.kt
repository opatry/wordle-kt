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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.ui.compose.theme.colorAbsent
import net.opatry.game.wordle.ui.compose.theme.colorCorrect
import net.opatry.game.wordle.ui.compose.theme.colorPresent
import net.opatry.game.wordle.ui.compose.theme.tileTextColor
import net.opatry.game.wordle.ui.compose.theme.white


@Composable
fun WordleGrid(grid: List<Answer>) {
    Column {
        grid.forEach { row ->
            WordleWordRow(row)
        }
    }
}

@Composable
fun WordleWordRow(row: Answer, modifier: Modifier = Modifier) {
    Row(modifier) {
        row.letters.forEachIndexed { index, char ->
            WordleCharCell(char, row.flags[index])
        }
    }
}

@Composable
fun AnswerFlag.cellColor(): Color = when (this) {
    AnswerFlag.EMPTY -> white
    AnswerFlag.PRESENT -> colorPresent
    AnswerFlag.ABSENT -> colorAbsent
    AnswerFlag.CORRECT -> colorCorrect
}

@Composable
fun WordleCharCell(char: Char, flag: AnswerFlag) {
    val backgroundColor by animateColorAsState(flag.cellColor())
    val (foregroundColor, borderColor) = when {
        flag == AnswerFlag.EMPTY && char.isWhitespace() ->
            Color.Transparent to MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.disabled)
        flag == AnswerFlag.EMPTY ->
            MaterialTheme.colors.onBackground to MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
        else ->
            tileTextColor to backgroundColor
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
            style = MaterialTheme.typography.h3
        )
    }
}
