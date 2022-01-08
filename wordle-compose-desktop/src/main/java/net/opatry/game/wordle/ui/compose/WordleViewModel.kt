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

class WordleViewModel(/*private val rules: Wordle*/) {
    var answer by mutableStateOf("")

    private var tries = 0
    private val maxTries = 6

    val alphabet = mutableMapOf<Char, AnswerFlag>().apply {
        var c = 'A'
        while (c <= 'Z') {
            this += c to AnswerFlag.values().random()
            ++c
        }
    }
    private val wordsData = mutableListOf<String>().apply { repeat(6) { add(EMPTY_WORD) } }
    var words by mutableStateOf<List<String>>(wordsData)

    var userInput by mutableStateOf("")

    private fun updateWords() {
        wordsData.apply {
            if (tries in indices) {
                this[tries] = userInput.padEnd(5, ' ')
            }
        }
        words = wordsData.toList()

    }

    fun updateUserInput(input: String) {
        if (tries >= maxTries) return

        val normalized = input.take(5).uppercase()
        if (normalized != userInput) {
            userInput = normalized
            updateWords()
        }
    }

    fun validateUserInput() {
        // TODO manage business logic
        if (tries < maxTries && userInput.length == 5) {
            ++tries
            userInput = ""
            updateWords()
        }
        answer = if (wordsData.any { it == "HELLO" } || tries == maxTries) "HELLO" else ""
    }

    companion object {
        private val EMPTY_WORD = "".padEnd(5, ' ')
    }

    fun restart() {
        tries = 0
        answer = ""
        userInput = ""
        wordsData.clear()
        wordsData.apply { repeat(6) { add(EMPTY_WORD) } }
        updateWords()
        // TODO reset alphabet
    }
}
