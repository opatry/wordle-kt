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

fun main() {
    var playing = true
    while (playing) {
        val rules = WordleRules(words)

        println(
            """
            .---------------.
            | Hello Wordle! |
            '---------------'
            """.trimIndent()
        )

        println(rules.state.toString())
        while (rules.state is State.Playing) {
            print(" ➡️ Enter a 5 letter english word: ")
            val word = readLine().toString()

            if (rules.isWordValid(word)) {
                rules.playWord(word)
            } else {
                println(" ❌ '$word' isn't in the list of words.")
            }
            println(rules.state.toString())
        }

        print(" 🔄 Play again? (y/N) ")
        playing = readLine().toString().equals("y", ignoreCase = true)
    }
}