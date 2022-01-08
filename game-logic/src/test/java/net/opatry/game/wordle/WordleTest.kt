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

private fun List<Answer>.toWords(): Array<String> = map { it.letters.concatToString() }.toTypedArray()

@RunWith(JUnit4::class)
class WordleTest {
    @Test
    fun `answer must be part of available words`() {
        assertThrows(IllegalArgumentException::class.java) {
            Wordle(emptyList(), "TOTOT")
        }
        Wordle(listOf("tOTot"), "tOTot")
        Wordle(listOf("TOTOT", "TITIT"), "TOTOT")
        Wordle(listOf("TUTUT", "TOTOT", "TITIT"), "TOTOT")
    }

    @Test
    fun `available words should all be 5 char long`() {
        assertThrows(IllegalArgumentException::class.java) {
            Wordle(listOf("A"), "A")
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle(listOf("ABCD"), "ABCD")
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle(listOf("ABCDEF"), "ABCDEF")
        }
        val game = Wordle(listOf("ABCDE"), "ABCDE", 0u)
        assertEquals("ABCDE", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `only latin letters characters are allowed`() {
        assertThrows(IllegalArgumentException::class.java) {
            Wordle(listOf("$$$$$"), "$$$$$")
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle(listOf("会会会会会"), "会会会会会")
        }
        val game = Wordle(listOf("ABCDE"), "ABCDE", 0u)
        assertEquals("ABCDE", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `answer is returned as uppercase`() {
        val game = Wordle(listOf("ABCDE"), "abcde", 0u)
        assertEquals("ABCDE", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `accented words are normalized before checking for invalid characters`() {
        // shouldn't throw IllegalArgumentException and accept "ANIME" as answer with "animé" in words
        val game = Wordle(listOf("animé"), "ANIME", 0u)
        assertEquals("ANIME", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `accented answer is normalized`() {
        // shouldn't throw IllegalArgumentException and accept "animé" as answer with "ANIME" in words
        val game = Wordle(listOf("ANIME"), "animé", 0u)
        assertEquals("ANIME", (game.state as? State.Lost)?.selectedWord)
    }

    @Test
    fun `initial state is playing and empty`() {
        val game = Wordle(listOf("TOTOT"), "TOTOT")
        val state = game.state as? State.Playing
        assertNotNull(state)
        assertEquals(emptyList<String>(), state?.answers)
    }

    @Test
    fun `too short word isn't a valid answer`() {
        val game = Wordle(listOf("TOTOT"), "TOTOT")
        assertFalse(game.isWordValid("TOTO"))
    }

    @Test
    fun `too long word isn't a valid answer`() {
        val game = Wordle(listOf("TOTOT"), "TOTOT")
        assertFalse(game.isWordValid("TOTOTOT"))
    }

    @Test
    fun `word not part of available words isn't a valid answer`() {
        val game = Wordle(listOf("TOTOT"), "TOTOT")
        assertFalse(game.isWordValid("TITIT"))
    }

    @Test
    fun `word part of available words is a valid answer`() {
        val game = Wordle(listOf("TOTOT", "TITIT"), "TOTOT")
        assertTrue(game.isWordValid("TITIT"))
    }

    @Test
    fun `selected word is a valid answer`() {
        val game = Wordle(listOf("TOTOT", "TUTUT"), "TUTUT")
        assertTrue(game.isWordValid("TUTUT"))
    }

    @Test
    fun `selected word accented is a valid answer`() {
        val game = Wordle(listOf("TOTOT", "TUTUT"), "TUTUT")
        assertTrue(game.isWordValid("  tûtüt "))
    }

    @Test
    fun `play word on an end state does nothing`() {
        val game = Wordle(listOf("TOTOT", "TUTUT"), "TUTUT", 0u)
        val originalState = game.state
        assertTrue(originalState !is State.Playing)
        assertFalse(game.playWord("TUTUT"))
        assertEquals(originalState, game.state)
    }

    @Test
    fun `play invalid word does nothing`() {
        val game = Wordle(listOf("TOTOT", "TUTUT"), "TUTUT")
        val originalState = game.state
        assertFalse(game.playWord("z"))
        assertEquals(originalState, game.state)
    }

    @Test
    fun `play valid word adds a new word to state`() {
        val game = Wordle(listOf("TOTOT", "TUTUT"), "TUTUT")
        val originalState = game.state
        assertTrue(game.playWord("TOTOT"))
        assertNotEquals(originalState, game.state)
        assertArrayEquals(arrayOf("TOTOT"), game.state.answers.toWords())
    }

    @Test
    fun `playing correct answer ends the game to Won state`() {
        val game = Wordle(listOf("TUTUT"), "TUTUT")
        assertTrue(game.playWord("TUTUT"))
        assertTrue(game.state is State.Won)
        assertEquals("TUTUT", (game.state as? State.Won)?.selectedWord)
        assertArrayEquals(arrayOf("TUTUT"), game.state.answers.toWords())
    }

    @Test
    fun `playing max allowed words without correct answer ends the game to Lost state`() {
        val game = Wordle(listOf("ERROR", "MISSS", "TUTUT"), "TUTUT", maxTries = 2u)
        assertTrue(game.playWord("ERROR"))
        assertTrue(game.state is State.Playing)
        assertArrayEquals(arrayOf("ERROR"), game.state.answers.toWords())
        assertTrue(game.playWord("MISSS"))
        assertTrue(game.state is State.Lost)
        assertEquals("TUTUT", (game.state as? State.Lost)?.selectedWord)
        assertArrayEquals(arrayOf("ERROR", "MISSS"), game.state.answers.toWords())
    }

    @Test
    fun `playing the same word twice consumes an answer`() {
        val game = Wordle(listOf("ERROR", "TUTUT"), "TUTUT")
        assertTrue(game.playWord("ERROR"))
        assertArrayEquals(arrayOf("ERROR"), game.state.answers.toWords())
        assertTrue(game.playWord("ERROR"))
        assertArrayEquals(arrayOf("ERROR", "ERROR"), game.state.answers.toWords())
    }
}
