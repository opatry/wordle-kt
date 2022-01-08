package net.opatry.game.wordle

enum class AnswerFlag {
    EMPTY,
    PRESENT,
    ABSENT,
    CORRECT;

    override fun toString(): String = when (this) {
        EMPTY -> "_"
        PRESENT -> "-"
        ABSENT -> " "
        CORRECT -> "+"
    }
}

class Answer(
    val letters: CharArray,
    val flags: Array<AnswerFlag>
) {

    override fun hashCode(): Int {
        var hash = 7
        hash = 31 * hash + letters.contentHashCode()
        hash = 31 * hash + flags.contentHashCode()
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (other is Answer) {
            return letters.contentEquals(other.letters)
                    && flags.contentEquals(other.flags)
        }
        return false
    }

    override fun toString(): String {
        return flags.joinToString("")
    }

    companion object {
        val EMPTY = Answer(CharArray(5) { ' ' }, Array(5) { AnswerFlag.EMPTY })
        fun computeAnswer(word: String, selectedWord: String): Answer {
            require(word.length == selectedWord.length) { "'$word' and '$selectedWord' should have the same size" }
            val flags = Array(word.length) { AnswerFlag.ABSENT }
            // need to go in 2 passes, 1 to spot correct position first, to ignore such position in next contains check
            // FIXME iterated on selectedWord rather than word for correct result?
            //  If 1 'e' is present in selected word and user inputs a word with 2 'e', one being correctly placed, the other shouldn't be yellow

            word.forEachIndexed { index, char ->
                if (char == selectedWord[index]) {
                    flags[index] = AnswerFlag.CORRECT
                }
            }
            word.forEachIndexed { index, char ->
                if (selectedWord.contains(char) && flags[index] != AnswerFlag.CORRECT) {
                    flags[index] = AnswerFlag.PRESENT
                }
            }
            return Answer(word.toCharArray(), flags)
        }
    }
}

private fun StringBuffer.appendAnswer(answer: Answer) {
    answer.letters.forEachIndexed { index, char ->
        append("$char${answer.flags[index].toEmoji()}")
    }
    append("\n")
}

sealed class State(
    open val answers: List<Answer>,
    open val maxTries: UInt
) {
    data class Playing(
        override val answers: List<Answer>,
        override val maxTries: UInt
    ) : State(answers, maxTries) {
        override fun toString(): String {
            return if (answers.isNotEmpty()) {
                super.toString() + "Keep going… ${answers.size}/$maxTries"
            } else {
                super.toString()
            }
        }
    }

    data class Won(
        override val answers: List<Answer>,
        override val maxTries: UInt,
        val wordleId: Int,
        val selectedWord: String
    ) :
        State(answers, maxTries) {
        override fun toString(): String {
            return super.toString() +
                    "Wordle $wordleId ${answers.size + 1}/$maxTries\n" +
                    "Congrats! You found the correct answer 🎉: $selectedWord"
        }
    }

    data class Lost(
        override val answers: List<Answer>,
        override val maxTries: UInt,
        val wordleId: Int,
        val selectedWord: String
    ) :
        State(answers, maxTries) {
        override fun toString(): String {
            return super.toString() +
                    "Wordle $wordleId X/$maxTries\n" +
                    "Doh! You didn't find the answer 🤭: $selectedWord"
        }
    }

    override fun toString(): String {
        val buffer = StringBuffer()
        answers.forEach(buffer::appendAnswer)
        repeat(maxTries.toInt() - answers.size) { buffer.appendAnswer(Answer.EMPTY) }
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
    private val wordleId = words.indexOf(selectedWord)
    var state: State = when {
        maxTries > 0u -> State.Playing(emptyList(), maxTries)
        else -> State.Lost(emptyList(), maxTries, wordleId, selectedWord)
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
            add(Answer.computeAnswer(sanitized, selectedWord))
        }.toList()

        state = when {
            sanitized == selectedWord -> State.Won(answers, maxTries, wordleId, selectedWord)
            answers.size.toUInt() == maxTries -> State.Lost(answers, maxTries, wordleId, selectedWord)
            else -> playingState.copy(answers = answers)
        }

        return true
    }
}
