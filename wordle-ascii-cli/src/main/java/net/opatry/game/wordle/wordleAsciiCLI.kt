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


val AnswerFlag.toEmoji: String
    get() = when (this) {
        AnswerFlag.NONE -> "⬜"
        AnswerFlag.PRESENT -> "🟨"
        AnswerFlag.ABSENT -> "⬛"
        AnswerFlag.CORRECT -> "🟩"
    }

private fun StringBuffer.appendAnswer(answer: Answer) {
    answer.letters.forEachIndexed { index, char ->
        append("$char${answer.flags[index].toEmoji}")
    }
    append("\n")
}

fun State.toString(wordleId: Int): String {
    val buffer = StringBuffer()
    val (prefix, suffix) = when (this) {
        is State.Won -> "Wordle $wordleId ${answers.size}/$maxTries" to "Congrats! You found the correct answer 🎉: $selectedWord"
        is State.Lost -> "Wordle $wordleId X/$maxTries" to "Doh! You didn't find the answer 🤭: $selectedWord"
        is State.Playing -> "" to if (answers.isNotEmpty()) "Keep going… ${answers.size}/$maxTries" else ""
    }

    if (prefix.isNotEmpty()) {
        buffer.append(prefix).append('\n')
    }

    answers.forEach(buffer::appendAnswer)
    val emptyAnswer = Answer(CharArray(wordSize) { ' ' }, Array(wordSize) { AnswerFlag.NONE })
    repeat(maxTries - answers.size) {
        buffer.appendAnswer(emptyAnswer)
    }

    if (suffix.isNotEmpty()) {
        buffer.append(suffix).append('\n')
    }

    return buffer.toString()
}

private val InputState.cause: String
    get() = when (this) {
        InputState.VALID -> ""
        InputState.TOO_SHORT -> "too short"
        InputState.TOO_LONG -> "too long"
        InputState.NOT_IN_DICTIONARY -> "not in dictionary"
        InputState.NOT_PLAYING -> "not playing"
    }

fun main() {
    var playing = true
    while (playing) {
        val wordleId = words.indices.random()
        val rules = WordleRules(words, words[wordleId])

        println(
            """
            .---------------.
            | Hello Wordle! |
            '---------------'
            """.trimIndent()
        )

        println(rules.state.toString(wordleId))
        while (rules.state is State.Playing) {
            print(" ➡️ Enter a 5 letter english word: ")
            val word = readLine().toString()

            val inputState = rules.isWordValid(word)
            if (inputState == InputState.VALID) {
                rules.playWord(word)
            } else {
                println(" ❌ '$word' is invalid: ${inputState.cause}")
            }
            println(rules.state.toString(wordleId))
        }

        print(" 🔄 Play again? (y/N) ")
        playing = readLine().toString().equals("y", ignoreCase = true)
    }
}