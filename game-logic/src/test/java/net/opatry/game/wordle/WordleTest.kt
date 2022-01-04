package net.opatry.game.wordle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
            Wordle { Answer(emptyList(), "TOTOTO") }
        }
        Wordle { Answer(listOf("tOTotO"), "tOTotO") }
        Wordle { Answer(listOf("TOTOTO", "TITITI"), "TOTOTO") }
        Wordle { Answer(listOf("TUTUTU", "TOTOTO", "TITITI"), "TOTOTO") }
    }

    @Test
    fun `available words should all be 6 char long`() {
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("A"), "A") }
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("ABCDE"), "ABCDE") }
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("ABCDEFG"), "ABCDEFG") }
        }
        val game = Wordle { Answer(listOf("ABCDEF"), "ABCDEF") }
        assertEquals("ABCDEF", game.answer.selectedWord)
    }

    @Test
    fun `only latin letters characters are allowed`() {
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("éééééé"), "éééééé") }
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("$$$$$$"), "$$$$$$") }
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("会会会会会会"), "会会会会会会") }
        }
        val game = Wordle { Answer(listOf("ABCDEF"), "ABCDEF") }
        assertEquals("ABCDEF", game.answer.selectedWord)
    }

    @Test
    fun `answer is returned as uppercase`() {
        Wordle { Answer(listOf("ABCDEF"), "ABCDEF") }
    }

    @Test
    fun `initial state is playing and empty`() {
        val answer = Answer(listOf("TOTOTO"), "TOTOTO")
        val game = Wordle { answer }
        val state = game.state as? State.Playing
        assertNotNull(state)
        assertEquals(emptyList<String>(), state?.answers)
    }

    @Test
    fun `too short word isn't a valid answer`() {
        val answer = Answer(listOf("TOTOTO"), "TOTOTO")
        val game = Wordle { answer }
        assertFalse(game.isWordValid("TOTO"))
    }

    @Test
    fun `too long word isn't a valid answer`() {
        val answer = Answer(listOf("TOTOTO"), "TOTOTO")
        val game = Wordle { answer }
        assertFalse(game.isWordValid("TOTOTOTO"))
    }

    @Test
    fun `word not part of available words isn't a valid answer`() {
        val answer = Answer(listOf("TOTOTO"), "TOTOTO")
        val game = Wordle { answer }
        assertFalse(game.isWordValid("TITITI"))
    }

    @Test
    fun `word part of available words is a valid answer`() {
        val answer = Answer(listOf("TOTOTO", "TITITI"), "TOTOTO")
        val game = Wordle { answer }
        assertTrue(game.isWordValid("TITITI"))
    }

    @Test
    fun `selected word is a valid answer`() {
        val answer = Answer(listOf("TOTOTO", "TUTUTU"), "TUTUTU")
        val game = Wordle { answer }
        assertTrue(game.isWordValid("TUTUTU"))
    }
}