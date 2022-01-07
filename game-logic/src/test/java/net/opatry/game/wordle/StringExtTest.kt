package net.opatry.game.wordle

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StringExtTest {
    @Test
    fun `toWordle makes text uppercase`() {
        assertEquals("A", "a".toWordle())
    }

    @Test
    fun `toWordle removes leading & trailing spaces`() {
        assertEquals("A", "  A  ".toWordle())
    }

    @Test
    fun `toWordle normalizes accented characters`() {
        assertEquals("A", "à".toWordle())
    }

    @Test
    fun `toWordle normalizes accented uppercase characters`() {
        assertEquals("A", "Ã".toWordle())
    }
}
