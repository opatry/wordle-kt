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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

private fun List<Answer>.toWords(): Array<String> = map { it.letters.concatToString() }.toTypedArray()

@RunWith(JUnit4::class)
class WordleTest {
    @Test
    fun `at least one word is required`() {
        assertThrows(IllegalArgumentException::class.java) {
            WordleRules(emptyList(), "")
        }
    }

    @Test
    fun `word shouldn't be empty`() {
        assertThrows(IllegalArgumentException::class.java) {
            WordleRules(listOf(""), "")
        }
    }

    @Test
    fun `answer must be part of available words`() {
        assertThrows(IllegalArgumentException::class.java) {
            WordleRules(listOf("TITIT"), "TOTOT")
        }
        WordleRules(listOf("tOTot"), "tOTot")
        WordleRules(listOf("TOTOT", "TITIT"), "TOTOT")
        WordleRules(listOf("TUTUT", "TOTOT", "TITIT"), "TOTOT")
    }

    @Test
    fun `all words must be the same length`() {
        assertThrows(IllegalArgumentException::class.java) {
            WordleRules(listOf("ABC", "DEFXYZ"), "ABC")
        }
        val game = WordleRules(listOf("ABCDE"), "ABCDE", 0)
        assertEquals("ABCDE", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `only latin letters characters are allowed`() {
        assertThrows(IllegalArgumentException::class.java) {
            WordleRules(listOf("$$$$$"), "$$$$$")
        }
        assertThrows(IllegalArgumentException::class.java) {
            WordleRules(listOf("会会会会会"), "会会会会会")
        }
        val game = WordleRules(listOf("ABCDE"), "ABCDE", 0)
        assertEquals("ABCDE", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `answer is returned as uppercase`() {
        val game = WordleRules(listOf("ABCDE"), "abcde", 0)
        assertEquals("ABCDE", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `accented words are normalized before checking for invalid characters`() {
        // shouldn't throw IllegalArgumentException and accept "ANIME" as answer with "animé" in words
        val game = WordleRules(listOf("animé"), "ANIME", 0)
        assertEquals("ANIME", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `accented answer is normalized`() {
        // shouldn't throw IllegalArgumentException and accept "animé" as answer with "ANIME" in words
        val game = WordleRules(listOf("ANIME"), "animé", 0)
        assertEquals("ANIME", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `initial state is playing and empty`() {
        val game = WordleRules(listOf("TOTOT"), "TOTOT")
        val state = game.state as? State.Playing
        assertNotNull(state)
        assertEquals(emptyList<String>(), state?.answers)
    }

    @Test
    fun `too short word isn't a valid answer`() {
        val game = WordleRules(listOf("TOTOT"), "TOTOT")
        assertEquals(InputState.TOO_SHORT, game.isWordValid("TOTO"))
    }

    @Test
    fun `too long word isn't a valid answer`() {
        val game = WordleRules(listOf("TOTOT"), "TOTOT")
        assertEquals(InputState.TOO_LONG, game.isWordValid("TOTOTOT"))
    }

    @Test
    fun `word not part of available words isn't a valid answer`() {
        val game = WordleRules(listOf("TOTOT"), "TOTOT")
        assertEquals(InputState.NOT_IN_DICTIONARY, game.isWordValid("TITIT"))
    }

    @Test
    fun `word part of available words is a valid answer`() {
        val game = WordleRules(listOf("TOTOT", "TITIT"), "TOTOT")
        assertEquals(InputState.VALID, game.isWordValid("TITIT"))
    }

    @Test
    fun `selected word is a valid answer`() {
        val game = WordleRules(listOf("TOTOT", "TUTUT"), "TUTUT")
        assertEquals(InputState.VALID, game.isWordValid("TUTUT"))
    }

    @Test
    fun `selected word accented is a valid answer`() {
        val game = WordleRules(listOf("TOTOT", "TUTUT"), "TUTUT")
        assertEquals(InputState.VALID, game.isWordValid("  tûtüt "))
    }

    @Test
    fun `play word on an end state does nothing`() {
        val game = WordleRules(listOf("TOTOT", "TUTUT"), "TUTUT", 0)
        val originalState = game.state
        assertTrue(originalState !is State.Playing)
        assertEquals(InputState.NOT_PLAYING, game.playWord("TUTUT"))
        assertEquals(originalState, game.state)
    }

    @Test
    fun `play invalid word does nothing`() {
        val game = WordleRules(listOf("TOTOT", "TUTUT"), "TUTUT")
        val originalState = game.state
        assertEquals(InputState.TOO_SHORT, game.playWord("z"))
        assertEquals(originalState, game.state)
    }

    @Test
    fun `play valid word adds a new word to state`() {
        val game = WordleRules(listOf("TOTOT", "TUTUT"), "TUTUT")
        val originalState = game.state
        assertEquals(InputState.VALID, game.playWord("TOTOT"))
        assertNotEquals(originalState, game.state)
        assertArrayEquals(arrayOf("TOTOT"), game.state.answers.toWords())
    }

    @Test
    fun `playing correct answer ends the game to Won state`() {
        val game = WordleRules(listOf("TUTUT"), "TUTUT")
        assertEquals(InputState.VALID, game.playWord("TUTUT"))
        assertTrue(game.state is State.Won)
        assertEquals("TUTUT", (game.state as? State.Won)?.selectedWord)
        assertArrayEquals(arrayOf("TUTUT"), game.state.answers.toWords())
    }

    @Test
    fun `playing max allowed words without correct answer ends the game to Lost state`() {
        val game = WordleRules(listOf("ERROR", "MISSS", "TUTUT"), "TUTUT", maxTries = 2)
        assertEquals(InputState.VALID, game.playWord("ERROR"))
        assertTrue(game.state is State.Playing)
        assertArrayEquals(arrayOf("ERROR"), game.state.answers.toWords())
        assertEquals(InputState.VALID, game.playWord("MISSS"))
        assertTrue(game.state is State.Lost)
        assertEquals("TUTUT", (game.state as? State.Lost)?.selectedWord)
        assertArrayEquals(arrayOf("ERROR", "MISSS"), game.state.answers.toWords())
    }

    @Test
    fun `playing the same word twice consumes an answer`() {
        val game = WordleRules(listOf("ERROR", "TUTUT"), "TUTUT")
        assertEquals(InputState.VALID, game.playWord("ERROR"))
        assertArrayEquals(arrayOf("ERROR"), game.state.answers.toWords())
        assertEquals(InputState.VALID, game.playWord("ERROR"))
        assertArrayEquals(arrayOf("ERROR", "ERROR"), game.state.answers.toWords())
    }

    @Test
    fun `test real game representative situation is properly handled`() {
        val game = WordleRules(listOf("AAAAA", "WEEDS", "SPEED"), "SPEED")

        // no in dictionary
        assertEquals(InputState.NOT_IN_DICTIONARY, game.playWord("BBBBB"))
        assertArrayEquals(null, game.state.answers.lastOrNull()?.flags)

        // in dictionary, no match
        assertEquals(InputState.VALID, game.playWord("AAAAA"))
        assertArrayEquals(Array(5) { AnswerFlag.ABSENT }, game.state.answers.lastOrNull()?.flags)

        // in dictionary exact and partial matches
        assertEquals(InputState.VALID, game.playWord("WEEDS"))
        assertArrayEquals(
            arrayOf(AnswerFlag.ABSENT, AnswerFlag.PRESENT, AnswerFlag.CORRECT, AnswerFlag.PRESENT, AnswerFlag.PRESENT),
            game.state.answers.lastOrNull()?.flags
        )

        // in dictionary selected word
        assertEquals(InputState.VALID, game.playWord("SPEED"))
        assertArrayEquals(Array(5) { AnswerFlag.CORRECT }, game.state.answers.lastOrNull()?.flags)
        assertTrue(game.state is State.Won)
    }
}
