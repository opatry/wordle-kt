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
import com.jakewharton.mosaic.runMosaic
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.opatry.game.wordle.State
import net.opatry.game.wordle.WordleRules
import net.opatry.game.wordle.mosaic.component.WordleGrid
import net.opatry.game.wordle.ui.WordleViewModel
import org.jline.terminal.TerminalBuilder

suspend fun main() = runMosaic {
    // TODO check terminal is compatible (eg. IDEA is not!)
    var playing = true
    val viewModel = WordleViewModel(WordleRules(listOf("Hello", "Great", "Tiles", "Tales")))

    setContent {
        GameScreen(viewModel)
    }

    withContext(Dispatchers.IO) {
        val terminal = TerminalBuilder.terminal()
        terminal.enterRawMode()
        terminal.reader().use { reader ->
            while (playing) {
                while (viewModel.state is State.Playing) {
                    val userInput = viewModel.userInput
                    val read = reader.read()
                    when (val char = read.toChar()) {
                        13.toChar(), '\n' -> viewModel.validateUserInput()
                        127.toChar(), '\b' -> if (userInput.isNotEmpty()) {
                            viewModel.updateUserInput(userInput.dropLast(1))
                        }

                        in 'a'..'z',
                        in 'A'..'Z' -> viewModel.updateUserInput(userInput + char)
                        // '?' -> viewModel.showHelp()
                        // ',' -> viewModel.showSettings()
                        // 27.toChar() -> break
                        else -> Unit // TODO display something to user
                    }
                }

                val read = reader.read()
                playing = read.toChar().equals('y', ignoreCase = true)
                if (playing) {
                    viewModel.restart()
                }
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: WordleViewModel) {
    Column {
        Text("")

        WordleGrid(viewModel.grid)

        when (val state = viewModel.state) {
            is State.Won -> {
                Text("Wordle <TODO_wordleId> ${state.answers.size}/${state.maxTries}")
                Text(viewModel.answer)
            }

            is State.Lost -> {
                Text("Wordle <TODO_wordleId> X/${state.maxTries}")
                Text(viewModel.answer)
            }

            is State.Playing -> {
                Text(" ➡️ Enter a 5 letter english word")
                Text("") // TODO display error here if any or define a placeholder on top of grid
            }
        }

//        viewModel.state.toClipboard()
//        println("Results copied to clipboard!")

        // there must be stable number of lines for nice UI state
        if (viewModel.state !is State.Playing) {
            Text(" 🔄 Play again? (y/N)? ${viewModel.userInput}")
        } else {
            Text("")
        }
    }
}
