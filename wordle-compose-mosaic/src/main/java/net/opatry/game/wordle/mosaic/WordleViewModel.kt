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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.InputState
import net.opatry.game.wordle.State
import net.opatry.game.wordle.WordleRules

class WordleViewModel(private var rules: WordleRules) {
    var state by mutableStateOf(rules.state)
    var answer by mutableStateOf("")
        private set
    var grid by mutableStateOf<List<Answer>>(emptyList())
        private set

    init {
        updateGrid()
    }

    private fun updateGrid() {
        val answers = rules.state.answers.toMutableList()
        val turn = answers.size
        val maxTries = rules.state.maxTries
        val wordSize = rules.wordSize
        val emptyAnswer = Answer(CharArray(wordSize) { ' ' }, Array(wordSize) { AnswerFlag.NONE })
        repeat(maxTries - turn) {
            answers += emptyAnswer
        }
        grid = answers.toList()
    }

    private fun updateAnswer() {
        answer = when (val state = rules.state) {
            is State.Playing -> ""
            is State.Lost -> state.selectedWord
            is State.Won -> state.selectedWord
        }
    }

    fun playWord(word: String) {
        val normalized = word.take(5).uppercase()
        // TODO indicate error when input isn't valid
        if (rules.playWord(normalized) == InputState.VALID) {
            updateGrid()
        }
        updateAnswer()
        state = rules.state
    }

    fun restart() {
        rules = WordleRules(rules.words)
        updateGrid()
        updateAnswer()
        state = rules.state
    }
}
