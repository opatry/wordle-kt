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

package net.opatry.game.wordle.mosaic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import net.opatry.game.wordle.State
import net.opatry.game.wordle.copyToClipboard
import net.opatry.game.wordle.mosaic.component.Alphabet
import net.opatry.game.wordle.mosaic.component.WordleGrid
import net.opatry.game.wordle.ui.WordleViewModel


@Composable
fun GameScreen(viewModel: WordleViewModel) {
    Column {
        Text("")

        Row {
            Text("    ")
            WordleGrid(viewModel.grid)
            Text("            ")
            Alphabet(viewModel.alphabet)
        }

        Text("")

        // There must be stable number of lines for nice UI state.
        // All states should display 3 lines.
        when (val state = viewModel.state) {
            is State.Won -> {
                LaunchedEffect(viewModel.state) {
                    viewModel.stateLabel.copyToClipboard()
                }

                Text("Wordle <TODO_wordleId> ${state.answers.size}/${state.maxTries}")
                Text("Results copied to clipboard!") // FIXME depends on copyToClipboard success
                Text(" 🔄 Play again? (y/N)?")
            }

            is State.Lost -> {
                Text("Wordle <TODO_wordleId> X/${state.maxTries}")
                Row {
                    Text("The answer was ")
                    Text(
                        viewModel.answer,
                        color = Color.BrightWhite,
                        background = Color.Green,
                        style = TextStyle.Bold
                    )
                }
                Text(" 🔄 Play again? (y/N)?")
            }

            is State.Playing -> {
                Text("") // TODO display error here if any or define a placeholder on top of grid
                Text("")
                Text(" ➡️ Enter a 5 letter english word")
            }
        }
    }
}
