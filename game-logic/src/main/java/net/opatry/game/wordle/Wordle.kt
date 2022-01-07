package net.opatry.game.wordle


sealed class State(open val answers: List<String>) {
    data class Playing(override val answers: List<String>) : State(answers)
    data class Won(override val answers: List<String>) : State(answers)
    data class Lost(override val answers: List<String>) : State(answers)
}

data class Answer(val words: List<String>,
                  val selectedWord: String = words.random()) {

    // TODO + see https://twitter.com/momoxmia/status/1479026969559789568?s=20 / https://wa11y.co/
    override fun toString(): String {
        return super.toString()
    }
}

class Wordle(answerProvider: () -> Answer) {
    val answer: Answer = answerProvider().run {
        copy(words = words.map(String::toWordle), selectedWord = selectedWord.toWordle())
    }
    val state: State = State.Playing(emptyList())

    init {
        with (answer) {
            require(words.all { it.matches(Regex("[A-Z]{5}")) }) {
                "All words should be compound of 5 latin letters"
            }
            require(words.contains(selectedWord)) {
                "Selected word ($selectedWord) isn't part of available words ($words)"
            }
        }
    }

    fun isWordValid(word: String): Boolean {
        return answer.words.contains(word.trim().uppercase())
    }
}
