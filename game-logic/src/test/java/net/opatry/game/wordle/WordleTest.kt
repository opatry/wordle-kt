package net.opatry.game.wordle

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WordleTest {
    @Test
    fun `answer must be part of available words`() {
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(emptyList(), "TOTOT") }
        }
        Wordle { Answer(listOf("tOTot"), "tOTot") }
        Wordle { Answer(listOf("TOTOT", "TITIT"), "TOTOT") }
        Wordle { Answer(listOf("TUTUT", "TOTOT", "TITIT"), "TOTOT") }
    }

    @Test
    fun `available words should all be 5 char long`() {
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("A"), "A") }
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("ABCD"), "ABCD") }
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("ABCDEF"), "ABCDEF") }
        }
        val game = Wordle(0u) { Answer(listOf("ABCDE"), "ABCDE") }
        assertEquals("ABCDE", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `only latin letters characters are allowed`() {
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("$$$$$"), "$$$$$") }
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("会会会会会"), "会会会会会") }
        }
        val game = Wordle(0u) { Answer(listOf("ABCDE"), "ABCDE") }
        assertEquals("ABCDE", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `answer is returned as uppercase`() {
        val game = Wordle(0u) { Answer(listOf("ABCDE"), "abcde") }
        assertEquals("ABCDE", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `accented words are normalized before checking for invalid characters`() {
        // shouldn't throw IllegalArgumentException and accept "ANIME" as answer with "animé" in words
        val game = Wordle(0u) { Answer(listOf("animé"), "ANIME") }
        assertEquals("ANIME", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `accented answer is normalized`() {
        // shouldn't throw IllegalArgumentException and accept "animé" as answer with "ANIME" in words
        val game = Wordle(0u) { Answer(listOf("ANIME"), "animé") }
        assertEquals("ANIME", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `initial state is playing and empty`() {
        val answer = Answer(listOf("TOTOT"), "TOTOT")
        val game = Wordle { answer }
        val state = game.state as? State.Playing
        assertNotNull(state)
        assertEquals(emptyList<String>(), state?.answers)
    }

    @Test
    fun `too short word isn't a valid answer`() {
        val answer = Answer(listOf("TOTOT"), "TOTOT")
        val game = Wordle { answer }
        assertFalse(game.isWordValid("TOTO"))
    }

    @Test
    fun `too long word isn't a valid answer`() {
        val answer = Answer(listOf("TOTOT"), "TOTOT")
        val game = Wordle { answer }
        assertFalse(game.isWordValid("TOTOTOT"))
    }

    @Test
    fun `word not part of available words isn't a valid answer`() {
        val answer = Answer(listOf("TOTOT"), "TOTOT")
        val game = Wordle { answer }
        assertFalse(game.isWordValid("TITIT"))
    }

    @Test
    fun `word part of available words is a valid answer`() {
        val answer = Answer(listOf("TOTOT", "TITIT"), "TOTOT")
        val game = Wordle { answer }
        assertTrue(game.isWordValid("TITIT"))
    }

    @Test
    fun `selected word is a valid answer`() {
        val answer = Answer(listOf("TOTOT", "TUTUT"), "TUTUT")
        val game = Wordle { answer }
        assertTrue(game.isWordValid("TUTUT"))
    }

    @Test
    fun `selected word accented is a valid answer`() {
        val answer = Answer(listOf("TOTOT", "TUTUT"), "TUTUT")
        val game = Wordle { answer }
        assertTrue(game.isWordValid("  tûtüt "))
    }

    @Test
    fun `play word on an end state does nothing`() {
        val answer = Answer(listOf("TOTOT", "TUTUT"), "TUTUT")
        val game = Wordle(maxTries = 0u) { answer }
        val originalState = game.state
        assertTrue(originalState !is State.Playing)
        assertFalse(game.playWord("TUTUT"))
        assertEquals(originalState, game.state)
    }

    @Test
    fun `play invalid word does nothing`() {
        val answer = Answer(listOf("TOTOT", "TUTUT"), "TUTUT")
        val game = Wordle { answer }
        val originalState = game.state
        assertFalse(game.playWord("z"))
        assertEquals(originalState, game.state)
    }

    @Test
    fun `play valid word adds a new word to state`() {
        val answer = Answer(listOf("TOTOT", "TUTUT"), "TUTUT")
        val game = Wordle { answer }
        val originalState = game.state
        assertTrue(game.playWord("TOTOT"))
        assertNotEquals(originalState, game.state)
        assertArrayEquals(arrayOf("TOTOT"), game.state.answers.toTypedArray())
    }

    @Test
    fun `playing correct answer ends the game to Won state`() {
        val answer = Answer(listOf("TUTUT"), "TUTUT")
        val game = Wordle { answer }
        assertTrue(game.playWord("TUTUT"))
        assertTrue(game.state is State.Won)
        assertEquals("TUTUT", (game.state as? State.Won)?.selectedWord)
        assertArrayEquals(arrayOf("TUTUT"), game.state.answers.toTypedArray())
    }

    @Test
    fun `playing max allowed words without correct answer ends the game to Lost state`() {
        val answer = Answer(listOf("ERROR", "MISSS", "TUTUT"), "TUTUT")
        val game = Wordle(maxTries = 2u) { answer }
        assertTrue(game.playWord("ERROR"))
        assertTrue(game.state is State.Playing)
        assertArrayEquals(arrayOf("ERROR"), game.state.answers.toTypedArray())
        assertTrue(game.playWord("MISSS"))
        assertTrue(game.state is State.Lost)
        assertEquals("TUTUT", (game.state as? State.Lost)?.selectedWord)
        assertArrayEquals(arrayOf("ERROR", "MISSS"), game.state.answers.toTypedArray())
    }

    @Test
    fun `playing the same word twice consumes an answer`() {
        val answer = Answer(listOf("ERROR", "TUTUT"), "TUTUT")
        val game = Wordle { answer }
        assertTrue(game.playWord("ERROR"))
        assertArrayEquals(arrayOf("ERROR"), game.state.answers.toTypedArray())
        assertTrue(game.playWord("ERROR"))
        assertArrayEquals(arrayOf("ERROR", "ERROR"), game.state.answers.toTypedArray())
    }
}
