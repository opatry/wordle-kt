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

package net.opatry.game.wordle.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.InputState
import net.opatry.game.wordle.State
import net.opatry.game.wordle.WordleRules

private val victoryMessages = arrayOf(
    "Genius",
    "Magnificent",
    "Impressive",
    "Splendid",
    "Great",
    "Phew"
)
private val State.message: String
    get() {
        val index = answers.size - 1
        return if (this is State.Won && index in victoryMessages.indices) {
            victoryMessages[index]
        } else {
            ""
        }
    }

private fun StringBuffer.appendAnswer(answer: Answer) {
    append(
        answer.flags.joinToString(
            separator = " ",
            postfix = "\n",
            transform = AnswerFlag::toEmoji
        )
    ).trimEnd()
}

private fun State.toResultString(): String {
    val buffer = StringBuffer()
    buffer.append(
        when (this) {
            is State.Lost -> "Wordle <TODO_wordleId> X/$maxTries\n"
            is State.Won -> "Wordle <TODO_wordleId> ${answers.size}/$maxTries\n"
            else -> ""
        }
    )
    answers.forEach(buffer::appendAnswer)

    return buffer.toString()
}

class WordleViewModel(private var rules: WordleRules) {
    var firstLaunch by mutableStateOf(true)
        private set
    var state by mutableStateOf(rules.state)
    val stateLabel: String
        get() = rules.state.toResultString()
    var victory by mutableStateOf(rules.state is State.Won)
        private set
    var answer by mutableStateOf("")
        private set
    var grid by mutableStateOf<List<Answer>>(emptyList())
        private set
    var userInput by mutableStateOf("")
        private set
    var alphabet by mutableStateOf(emptyMap<Char, AnswerFlag>())

    private val _userFeedback = mutableListOf<String>()
    var userFeedback by mutableStateOf(_userFeedback.toList())
        private set

    init {
        updateGrid()
        updateAlphabet()
    }

    private fun updateGrid() {
        val answers = rules.state.answers.toMutableList()
        val turn = answers.size
        val maxTries = rules.state.maxTries.toInt()
        val wordSize = rules.wordSize
        if (turn < maxTries) {
            answers += Answer(userInput.padEnd(wordSize, ' ').toCharArray(), Array(wordSize) { AnswerFlag.NONE })
        }
        val emptyAnswer = Answer(CharArray(wordSize) { ' ' }, Array(wordSize) { AnswerFlag.NONE })
        repeat(maxTries - turn - 1) {
            answers += emptyAnswer
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
        if (rules.state !is State.Playing) return

        val normalized = input.take(rules.wordSize).uppercase()
        if (normalized != userInput) {
            userInput = normalized
            updateGrid()
        }
    }

    fun validateUserInput() {
        if (rules.state !is State.Playing) return

        when (rules.playWord(userInput)) {
            InputState.VALID -> {
                userInput = ""
                updateGrid()
                updateAlphabet()
            }

            InputState.NOT_IN_DICTIONARY -> {
                _userFeedback.add("Not in word list")
                userFeedback = _userFeedback.toList()
                updateGrid()
            }

            InputState.TOO_SHORT -> {
                _userFeedback.add("Not enough letters")
                userFeedback = _userFeedback.toList()
                updateGrid()
            }

            else -> Unit
        }
        val oldVictory = victory
        updateAnswer()

        // notify user
        victory = rules.state is State.Won
        if (victory && !oldVictory) {
            _userFeedback.add(rules.state.message)
            userFeedback = _userFeedback.toList()
        }
        updateAnswer()
        state = rules.state
    }

    fun restart() {
        rules = WordleRules(rules.words)
        victory = rules.state is State.Won
        userInput = ""
        _userFeedback.clear()
        userFeedback = _userFeedback.toList()
        updateGrid()
        updateAlphabet()
        updateAnswer()
        state = rules.state
    }

    fun consumed(message: String) {
        _userFeedback.remove(message)
        userFeedback = _userFeedback.toList()
    }

    fun firstLaunchDone() {
        firstLaunch = false
    }
}
