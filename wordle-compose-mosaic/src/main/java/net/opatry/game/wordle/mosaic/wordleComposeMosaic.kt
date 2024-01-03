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

import com.jakewharton.mosaic.runMosaic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.opatry.game.wordle.State
import net.opatry.game.wordle.WordleRules
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
