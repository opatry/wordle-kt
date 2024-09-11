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
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import net.opatry.game.wordle.AnswerFlag

@Composable
fun Alphabet(alphabet: Map<Char, AnswerFlag>) {
    Column {
        val colCount = 9
        val cellWidth = 5
        Text("╭" + "─".repeat(cellWidth * colCount) + "╮")
        alphabet.keys.chunked(colCount).forEachIndexed { index, row ->
            if (index > 0) {
                Text("│" + "     ".repeat(colCount) + "│")
            }
            Row {
                Text("│")
                row.forEach { letter ->
                    // cell width is leading & trailing space + WordleCharCell compound of 3 characters
                    Text(" ")
                    WordleCharCell(letter, alphabet[letter]!!)
                    Text(" ")
                }
                // pad empty space for partial rows to align left border
                repeat(colCount - row.size) {
                    Text(" ".repeat(cellWidth))
                }
                Text("│")
            }
        }
        Text("╰" + "─".repeat(cellWidth * colCount) + "╯")
    }
}
