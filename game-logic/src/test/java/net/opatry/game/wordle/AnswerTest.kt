package net.opatry.game.wordle

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AnswerTest {
    @Test
    fun `all different is all wrong`() {
        assertArrayEquals(Array(5) { AnswerFlag.WRONG }, Answer.computeAnswer("aaaaa", "bbbbb").flags)
    }

    @Test
    fun `all same is all correct`() {
        assertArrayEquals(Array(5) { AnswerFlag.CORRECT }, Answer.computeAnswer("aaaaa", "aaaaa").flags)
    }
}