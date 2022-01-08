package net.opatry.game.wordle

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StringExtTest {
    @Test
    fun `sanitizeForWordle makes text uppercase`() {
        assertEquals("A", "a".sanitizeForWordle())
    }

    @Test
    fun `sanitizeForWordle removes leading & trailing spaces`() {
        assertEquals("A", "  A  ".sanitizeForWordle())
    }

    @Test
    fun `sanitizeForWordle normalizes accented characters`() {
        assertEquals("A", "à".sanitizeForWordle())
    }

    @Test
    fun `sanitizeForWordle normalizes accented uppercase characters`() {
        assertEquals("A", "Ã".sanitizeForWordle())
    }
}
