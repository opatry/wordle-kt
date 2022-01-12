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

package net.opatry.game.wordle

enum class AnswerFlag {
    NONE,
    PRESENT,
    ABSENT,
    CORRECT
}

class Answer(
    val letters: CharArray,
    val flags: Array<AnswerFlag>
) {

    override fun hashCode(): Int {
        var hash = 7
        hash = 31 * hash + letters.contentHashCode()
        hash = 31 * hash + flags.contentHashCode()
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (other is Answer) {
            return letters.contentEquals(other.letters)
                    && flags.contentEquals(other.flags)
        }
        return false
    }

    override fun toString(): String {
        return flags.joinToString("")
    }

    companion object {
        val EMPTY = Answer(CharArray(5) { ' ' }, Array(5) { AnswerFlag.NONE })
        fun computeAnswer(word: String, selectedWord: String): Answer {
            require(word.length == selectedWord.length) { "'$word' and '$selectedWord' should have the same size" }
            val flags = Array(word.length) { AnswerFlag.ABSENT }

            // each time a letter is flagged (CORRECT or PRESENT), we remove from here
            // it allows managing multiple occurrence of the same letter more easily
            val candidates = selectedWord.toMutableList()

            // need to go in two passes, first to flag correct position
            // to ignore such indices in next contains iteration
            word.forEachIndexed { index, char ->
                if (char == selectedWord[index]) {
                    flags[index] = AnswerFlag.CORRECT
                    candidates -= char
                }
            }

            for (index in word.indices) {
                if (flags[index] == AnswerFlag.CORRECT) continue

                val char = word[index]
                if (char in candidates) {
                    flags[index] = AnswerFlag.PRESENT
                    candidates -= char
                }
            }
            return Answer(word.toCharArray(), flags)
        }
    }
}

private fun StringBuffer.appendAnswer(answer: Answer) {
    answer.letters.forEachIndexed { index, char ->
        append("$char${answer.flags[index].toEmoji()}")
    }
    append("\n")
}

sealed class State(
    open val answers: List<Answer>,
    open val maxTries: UInt
) {
    data class Playing(
        override val answers: List<Answer>,
        override val maxTries: UInt
    ) : State(answers, maxTries) {
        override fun toString(): String {
            return if (answers.isNotEmpty()) {
                super.toString() + "Keep going… ${answers.size}/$maxTries"
            } else {
                super.toString()
            }
        }
    }

    data class Won(
        override val answers: List<Answer>,
        override val maxTries: UInt,
        val wordleId: Int,
        val selectedWord: String
    ) :
        State(answers, maxTries) {
        override fun toString(): String {
            return "Wordle $wordleId ${answers.size}/$maxTries\n" +
                    super.toString() +
                    "Congrats! You found the correct answer 🎉: $selectedWord"
        }
    }

    data class Lost(
        override val answers: List<Answer>,
        override val maxTries: UInt,
        val wordleId: Int,
        val selectedWord: String
    ) :
        State(answers, maxTries) {
        override fun toString(): String {
            return "Wordle $wordleId X/$maxTries\n" +
                    super.toString() +
                    "Doh! You didn't find the answer 🤭: $selectedWord"
        }
    }

    override fun toString(): String {
        val buffer = StringBuffer()
        answers.forEach(buffer::appendAnswer)
        repeat(maxTries.toInt() - answers.size) { buffer.appendAnswer(Answer.EMPTY) }
        return buffer.toString()
    }
}

enum class InputState {
    TOO_SHORT,
    TOO_LONG,
    NOT_IN_DICTIONARY,
    NOT_PLAYING,
    VALID
}

class WordleRules(
    inWords: List<String>,
    inSelectedWord: String = inWords.random(),
    private val maxTries: UInt = 6u
) {
    val words = inWords.map(String::sanitizeForWordle).distinct()
    private val selectedWord = inSelectedWord.sanitizeForWordle()
    private val wordleId = words.indexOf(selectedWord)
    var state: State = when {
        maxTries > 0u -> State.Playing(emptyList(), maxTries)
        else -> State.Lost(emptyList(), maxTries, wordleId, selectedWord)
    }
        private set

    init {
        require(words.all { it.matches(Regex("^[A-Z]{5}$")) }) {
            "All words should be compound of 5 latin letters"
        }
        require(words.contains(selectedWord)) {
            "Selected word ($selectedWord) isn't part of available words ($words)"
        }
    }

    fun isWordValid(word: String): InputState {
        val sanitized = word.sanitizeForWordle()
        // when checking for a word, no need to consider the game state
        return when {
            sanitized.length < 5 -> InputState.TOO_SHORT
            sanitized.length > 5 -> InputState.TOO_LONG
            !words.contains(sanitized) -> InputState.NOT_IN_DICTIONARY
            else -> InputState.VALID
        }
    }

    fun playWord(word: String): InputState {
        val playingState = state as? State.Playing ?: return InputState.NOT_PLAYING

        val sanitized = word.sanitizeForWordle()
        val inputState = isWordValid(sanitized)
        if (inputState != InputState.VALID) return inputState

        val answers = playingState.answers.toMutableList().apply {
            add(Answer.computeAnswer(sanitized, selectedWord))
        }.toList()

        state = when {
            sanitized == selectedWord -> State.Won(answers, maxTries, wordleId, selectedWord)
            answers.size.toUInt() == maxTries -> State.Lost(answers, maxTries, wordleId, selectedWord)
            else -> playingState.copy(answers = answers)
        }

        return inputState
    }
}
