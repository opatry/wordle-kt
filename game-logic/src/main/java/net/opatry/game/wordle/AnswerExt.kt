package net.opatry.game.wordle

// TODO + see https://twitter.com/momoxmia/status/1479026969559789568?s=20 / https://wa11y.co/
fun AnswerFlag.toEmoji(): String = when (this) {
    AnswerFlag.EMPTY -> "⬜"
    AnswerFlag.PRESENT -> "🟨"
    AnswerFlag.ABSENT -> "⬛"
    AnswerFlag.CORRECT -> "🟩"
}