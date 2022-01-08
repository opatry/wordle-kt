package net.opatry.game.wordle


sealed class State(open val answers: List<String>, open val maxTries: UInt) {
    data class Playing(override val answers: List<String>, override val maxTries: UInt) : State(answers, maxTries) {
        override fun toString(): String {
            return if (answers.isNotEmpty()) {
                super.toString() + "Keep going… ${answers.size}/$maxTries"
            } else {
                super.toString()
            }
        }
    }

    data class Won(override val answers: List<String>, override val maxTries: UInt, val selectedWord: String) :
        State(answers, maxTries) {
        override fun toString(): String {
            return super.toString() + "Congrats! You found the correct answer 🎉: $selectedWord"
        }
    }

    data class Lost(override val answers: List<String>, override val maxTries: UInt, val selectedWord: String) :
        State(answers, maxTries) {
        override fun toString(): String {
            return super.toString() + "Doh! You didn't find the answer 🤭: $selectedWord"
        }
    }

    private fun StringBuffer.appendWord(word: String) {
        word.toCharArray().joinTo(this, "") { "[ $it ]" }
        append("\n")
    }

    override fun toString(): String {
        val buffer = StringBuffer()
        answers.forEach { buffer.appendWord(it) }
        repeat(maxTries.toInt() - answers.size) { buffer.appendWord("     ") }
        return buffer.toString()
    }
}

class Wordle(
    inWords: List<String>,
    inSelectedWord: String = inWords.random(),
    private val maxTries: UInt = 6u
) {
    private val words = inWords.map(String::sanitizeForWordle)
    private val selectedWord = inSelectedWord.sanitizeForWordle()
    var state: State = when {
        maxTries > 0u -> State.Playing(emptyList(), maxTries)
        else -> State.Lost(emptyList(), maxTries, selectedWord)
    }
        private set

    init {
        require(words.all { it.matches(Regex("^[A-Z]{5}$")) }) {
            "All words should be compound of 5 latin letters"
        }
        require(words.contains(selectedWord)) {
            "Selected word ($selectedWord) isn't part of available words ($words)"
        }
    }

    fun isWordValid(word: String): Boolean {
        return words.contains(word.sanitizeForWordle())
    }

    fun playWord(word: String): Boolean {
        val sanitized = word.sanitizeForWordle()
        if (!isWordValid(sanitized)) return false
        val playingState = state as? State.Playing ?: return false

        val answers = playingState.answers.toMutableList().apply {
            add(sanitized)
        }.toList()

        state = when {
            sanitized == selectedWord -> State.Won(answers, maxTries, selectedWord)
            answers.size.toUInt() == maxTries -> State.Lost(answers, maxTries, selectedWord)
            else -> playingState.copy(answers = answers)
        }

        return true
    }
}
