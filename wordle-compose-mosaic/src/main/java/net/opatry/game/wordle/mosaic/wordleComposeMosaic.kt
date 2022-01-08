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
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.runMosaic
import kotlinx.coroutines.delay
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.State
import net.opatry.game.wordle.WordleRules
import net.opatry.game.wordle.ui.toEmoji

private fun StringBuffer.appendClipboardAnswer(answer: Answer) {
    answer.flags.map(AnswerFlag::toEmoji).forEach(::append)
    append('\n')
}

fun State.toClipboard(): String {
    val buffer = StringBuffer()
    buffer.append(
        when (this) {
            is State.Lost -> "Wordle <TODO_wordleId> X/$maxTries\n"
            is State.Won -> "Wordle <TODO_wordleId> ${answers.size}/$maxTries\n"
            else -> ""
        }
    )
    answers.forEach(buffer::appendClipboardAnswer)

    val clipboard = buffer.toString()

    // FIXME might not be cross platform/portable
    // TODO "pbcopy <<< $clipboard".runCommand(File(System.getProperty("user.dir")))
    return clipboard
}

suspend fun main() = runMosaic {
    // TODO check terminal is compatible (eg. IDEA is not!)
    var playing = true
    val viewModel = WordleViewModel(WordleRules(listOf("Hello", "Great", "Tiles", "Tales")))

    setContent {
        GameScreen(viewModel)
    }

    while (playing) {
        delay(16)
        while (viewModel.state is State.Playing) {
            delay(16)
            print(" ➡️ Enter a 5 letter english word: ") // FIXME shouldn't be done with compose/mosaic
            val word = readLine().toString() // FIXME how to scan with compose/mosaic
//            delay(16)
            viewModel.playWord(word)
        }

        delay(16)

        // TODO
//        viewModel.state.toClipboard()
//        println("Results copied to clipboard!")
        println(viewModel.state.toClipboard())

        print(" 🔄 Play again? (y/N) ") // FIXME shouldn't be done with compose/mosaic
        playing = readLine().toString().equals("y", ignoreCase = true) // FIXME how to scan with compose/mosaic
//        delay(16)
        if (playing) {
            viewModel.restart()
        }
    }
}

@Composable
fun GameScreen(viewModel: WordleViewModel) {
    Column {
        Toolbar()
        AnswerPlaceHolder(viewModel.answer)
        WordleGrid(viewModel.grid)
    }
}

@Composable
fun Toolbar() {
    Text("Wordle")
}

@Composable
fun AnswerPlaceHolder(answer: String) {
    Text(answer)
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

fun AnswerFlag.cellColors(): Pair<Color, Color> = when (this) {
    AnswerFlag.NONE -> Color.Black to Color.White
    AnswerFlag.PRESENT -> Color.Black to Color.Yellow
    AnswerFlag.ABSENT -> Color.White to Color.Black
    AnswerFlag.CORRECT -> Color.Black to Color.Green
}

@Composable
fun WordleCharCell(char: Char, flag: AnswerFlag) {
    val (foregroundColor, backgroundColor) = flag.cellColors()

    // TODO AnnotatedString " $char " https://github.com/JakeWharton/mosaic/issues/9
    Column {
        Row {
            Text(" ")
            Text(
                " $char ",
                color = foregroundColor,
                background = backgroundColor
            )
            Text(" ")
        }
        Text("    ")
    }
}
