package net.opatry.game.wordle

fun main() {
    var playing = true
    while (playing) {
        val game = Wordle { Answer(words) }

        println(
            """
            .---------------.
            | Hello Wordle! |
            '---------------'
            """.trimIndent()
        )

        while (game.state is State.Playing) {
            print(" ➡️ Enter a 5 letter english word: ")
            val word = readLine().toString()

            if (game.isWordValid(word)) {
                game.playWord(word)
            } else {
                println(" ❌ '$word' isn't in the list of words.")
            }
            println(game.state.toString())
        }

        print(" 🔄 Play again? (y/N) ")
        playing = readLine().toString().equals("y", ignoreCase = true)
    }
}