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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.InputState
import net.opatry.game.wordle.State
import net.opatry.game.wordle.WordleRules

class WordleViewModel(private var rules: WordleRules) {
    var answer by mutableStateOf("")
        private set
    var grid by mutableStateOf<List<Answer>>(emptyList())
        private set
    var userInput by mutableStateOf("")
        private set
    var alphabet by mutableStateOf(emptyMap<Char, AnswerFlag>())

    init {
        updateGrid()
        updateAlphabet()
    }

    private fun updateGrid() {
        val answers = rules.state.answers.toMutableList()
        val turn = answers.size
        val maxTries = rules.state.maxTries.toInt()
        if (turn < maxTries) {
            answers += Answer(userInput.padEnd(5, ' ').toCharArray(), Array(5) { AnswerFlag.NONE })
        }
        repeat(maxTries - turn - 1) {
            answers += Answer.EMPTY
        }
        grid = answers.toList()
    }

    private fun updateAlphabet() {
        // TODO couldn't we make this smarter?
        val answers = rules.state.answers
        val absent = mutableSetOf<Char>()
        val present = mutableSetOf<Char>()
        val correct = mutableSetOf<Char>()

        answers.forEach {
            it.flags.forEachIndexed { index, flag ->
                when (flag) {
                    AnswerFlag.ABSENT -> absent += it.letters[index]
                    AnswerFlag.PRESENT -> present += it.letters[index]
                    AnswerFlag.CORRECT -> correct += it.letters[index]
                    else -> Unit
                }
            }
        }

        val alphabet = mutableMapOf<Char, AnswerFlag>().apply {
            var c = 'A'
            while (c <= 'Z') {
                this += c to when {
                    correct.contains(c) -> AnswerFlag.CORRECT
                    present.contains(c) -> AnswerFlag.PRESENT
                    absent.contains(c) -> AnswerFlag.ABSENT
                    else -> AnswerFlag.NONE
                }
                ++c
            }
        }
        this.alphabet = alphabet.toMap()
    }

    private fun updateAnswer() {
        answer = when (val state = rules.state) {
            is State.Playing -> ""
            is State.Lost -> state.selectedWord
            is State.Won -> state.selectedWord
        }
    }

    fun updateUserInput(input: String) {
        val normalized = input.take(5).uppercase()
        if (normalized != userInput) {
            userInput = normalized
            updateGrid()
        }
    }

    fun validateUserInput() {
        when (rules.playWord(userInput)) {
            InputState.VALID -> {
                userInput = ""
                updateGrid()
                updateAlphabet()
            }
            InputState.NOT_IN_DICTIONARY -> {
                // TODO send an EVENT to indicate the error cause (to display a Toast)
                updateGrid()
            }
            InputState.TOO_SHORT -> {
                // TODO send an EVENT to indicate the error cause (to display a Toast)
                updateGrid()
            }
            else -> Unit
        }
        updateAnswer()
    }

    fun restart() {
        rules = WordleRules(rules.words)
        userInput = ""
        updateGrid()
        updateAlphabet()
        updateAnswer()
    }
}
