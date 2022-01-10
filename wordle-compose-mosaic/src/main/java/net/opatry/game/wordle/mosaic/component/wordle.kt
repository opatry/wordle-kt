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

package net.opatry.game.wordle.mosaic.component

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag


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

fun AnswerFlag.cellColors(): Pair<Color, Color> = when (this) {
    AnswerFlag.NONE -> Color.Black to Color.White
    AnswerFlag.PRESENT -> Color.Black to Color.Yellow
    AnswerFlag.ABSENT -> Color.BrightWhite to Color.Black
    AnswerFlag.CORRECT -> Color.BrightWhite to Color.Green
}

@Composable
fun WordleCharCell(char: Char, flag: AnswerFlag) {
    val (foregroundColor, backgroundColor) =
        if (flag == AnswerFlag.NONE && !char.isWhitespace())
            Color.Black to Color.BrightWhite
        else
            flag.cellColors()

    // TODO AnnotatedString " $char " https://github.com/JakeWharton/mosaic/issues/9
    Column {
        Row {
            Text(" ")
            Text(
                " $char ",
                color = foregroundColor,
                background = backgroundColor,
                style = TextStyle.Bold
            )
            Text(" ")
        }
        Text("")
    }
}
