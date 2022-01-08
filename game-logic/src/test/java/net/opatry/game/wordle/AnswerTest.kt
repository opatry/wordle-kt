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

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AnswerTest {
    @Test
    fun `all different is all wrong`() {
        assertArrayEquals(Array(5) { AnswerFlag.ABSENT }, Answer.computeAnswer("aaaaa", "bbbbb").flags)
    }

    @Test
    fun `all same is all correct`() {
        assertArrayEquals(Array(5) { AnswerFlag.CORRECT }, Answer.computeAnswer("aaaaa", "aaaaa").flags)
    }

    @Test
    fun `a letter is taken into account at most once`() {
        // if 1 letter is present in selected word and user inputs a word with this letter twice, one being correctly placed, the other shouldn't be considered
        val expected =
            arrayOf(AnswerFlag.ABSENT, AnswerFlag.CORRECT, AnswerFlag.ABSENT, AnswerFlag.ABSENT, AnswerFlag.ABSENT)
        assertArrayEquals(expected, Answer.computeAnswer("weeds", "hello").flags)
    }

    @Test
    fun `several letters in common are properly handled`() {
        // 1 properly placed, 2 others misplaced, misplaced 'e' comes first
        assertArrayEquals(
            arrayOf(AnswerFlag.ABSENT, AnswerFlag.PRESENT, AnswerFlag.CORRECT, AnswerFlag.PRESENT, AnswerFlag.PRESENT),
            Answer.computeAnswer("weeds", "speed").flags
        )
    }

    @Test
    fun `several letters in common are properly handled variation`() {
        // 1 properly placed, 2 others misplaced, properly placed 'e' comes first
        assertArrayEquals(
            arrayOf(AnswerFlag.PRESENT, AnswerFlag.ABSENT, AnswerFlag.CORRECT, AnswerFlag.PRESENT, AnswerFlag.PRESENT),
            Answer.computeAnswer("speed", "weeds").flags
        )
    }
}