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
        val game = Wordle { Answer(listOf("ABCDE"), "ABCDE") }
        assertEquals("ABCDE", game.answer.selectedWord)
    }

    @Test
    fun `only latin letters characters are allowed`() {
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("ééééé"), "ééééé") }
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("$$$$$"), "$$$$$") }
        }
        assertThrows(IllegalArgumentException::class.java) {
            Wordle { Answer(listOf("会会会会会"), "会会会会会") }
        }
        val game = Wordle { Answer(listOf("ABCDE"), "ABCDE") }
        assertEquals("ABCDE", game.answer.selectedWord)
    }

    @Test
    fun `answer is returned as uppercase`() {
        val game = Wordle { Answer(listOf("ABCDE"), "abcde") }
        assertEquals("ABCDE", game.answer.selectedWord)
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
}