/*
 * Copyright (c) 2022 Olivier Patry
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.opatry.game.wordle.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.InputState
import net.opatry.game.wordle.State
import net.opatry.game.wordle.WordleRules
import net.opatry.game.wordle.WordleStats
import net.opatry.game.wordle.data.WordleRecord
import net.opatry.game.wordle.data.WordleRepository
import net.opatry.game.wordle.sanitizeForWordle
import net.opatry.game.wordle.stats
import java.util.*

private val victoryMessages = arrayOf(
    "Genius",
    "Magnificent",
    "Impressive",
    "Splendid",
    "Great",
    "Phew"
)
private val State.message: String
    get() {
        val index = answers.size - 1
        return if (this is State.Won && index in victoryMessages.indices) {
            victoryMessages[index]
        } else {
            ""
        }
    }

enum class AppDialog {
    SETTINGS_PANEL,
    HOWTO_PANEL,
    HOWTO_DIALOG,
    STATS_DIALOG,
}

class WordleViewModel(inDictionary: List<String>, private val repository: WordleRepository) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val dictionary = inDictionary.map(kotlin.String::sanitizeForWordle)
    private val availableWords: List<String>
        get() = dictionary - repository.allRecords.map(WordleRecord::answer).toSet()
    val lastRecord: WordleRecord?
        get() = repository.allRecords.lastOrNull()
    private var wordleId: Int = -1
    private var rules: WordleRules? = null
    var loading by mutableStateOf(true)
        private set
    var statistics: WordleStats by mutableStateOf(repository.allRecords.stats())
        private set
    val victory: Boolean
        get() = rules?.state is State.Won
    private val answer: String
        get() = when (val state = rules?.state) {
            is State.Lost -> state.selectedWord
            is State.Won -> state.selectedWord
            else -> ""
        }
    var endOfGameAnswer by mutableStateOf("")
        private set
    var canRestart by mutableStateOf(false)
        private set
    var grid by mutableStateOf<List<Answer>>(emptyList())
        private set
    var userInput by mutableStateOf("")
        private set
    var alphabet by mutableStateOf(emptyMap<Char, AnswerFlag>())

    private val _userFeedback = mutableListOf<String>()
    var userFeedback by mutableStateOf(_userFeedback.toList())
        private set
    var requestedDialog by mutableStateOf<AppDialog?>(null)
        private set
    private val _actionsEnabled: Boolean
        get() = rules != null && grid.isNotEmpty() && alphabet.isNotEmpty() && requestedDialog == null
    var actionsEnabled by mutableStateOf(_actionsEnabled)
        private set

    init {
        scope.launch(Dispatchers.Main) {
            repository.loadRecords()
            val records = repository.allRecords
            statistics = records.stats()
            if (records.isEmpty()) {
                requestedDialog = AppDialog.HOWTO_DIALOG
            }
            restart()
            loading = false
        }

        // TODO while repository is loading, we should give feedback to user and it could also be the right time
        //  to ask for preferred game mode (language, word size)
    }

    private fun updateGrid() {
        val rules = rules ?: return

        val answers = rules.state.answers.toMutableList()
        val turn = answers.size
        val maxTries = rules.state.maxTries
        val wordSize = rules.wordSize
        if (turn < maxTries) {
            answers += Answer(userInput.padEnd(wordSize, ' ').toCharArray(), Array(wordSize) { AnswerFlag.NONE })
        }
        val emptyAnswer = Answer(CharArray(wordSize) { ' ' }, Array(wordSize) { AnswerFlag.NONE })
        repeat(maxTries - turn - 1) {
            answers += emptyAnswer
        }
        grid = answers.toList()
    }

    private fun updateAlphabet() {
        val rules = rules ?: return

        // TODO couldn't we make this smarter?
        val answers = rules.state.answers
        val absent = mutableSetOf<Char>()
        val present = mutableSetOf<Char>()
        val correct = mutableSetOf<Char>()

        answers.forEach {
            it.flags.forEachIndexed { index, flag ->
                when (flag) {
                    AnswerFlag.ABSENT -> absent += it.letters[index]
                    AnswerFlag.PRESENT -> present += it.letters[index]
                    AnswerFlag.CORRECT -> correct += it.letters[index]
                    else -> Unit
                }
            }
        }

        val alphabet = mutableMapOf<Char, AnswerFlag>().apply {
            var c = 'A'
            while (c <= 'Z') {
                this += c to when {
                    correct.contains(c) -> AnswerFlag.CORRECT
                    present.contains(c) -> AnswerFlag.PRESENT
                    absent.contains(c) -> AnswerFlag.ABSENT
                    else -> AnswerFlag.NONE
                }
                ++c
            }
        }
        this.alphabet = alphabet.toMap()
    }

    private fun updateEndOfGame() {
        val rules = rules ?: return
        endOfGameAnswer = if (rules.state is State.Lost) {
            answer
        } else {
            ""
        }

        canRestart = rules.state !is State.Playing
    }

    fun updateUserInput(input: String) {
        val rules = rules ?: return

        if (rules.state !is State.Playing) return

        val normalized = input.take(rules.wordSize).uppercase()
        if (normalized != userInput) {
            userInput = normalized
            updateGrid()
        }
    }

    fun validateUserInput() {
        val rules = rules
        if (rules == null || rules.state !is State.Playing) return

        val oldVictory = victory

        when (rules.playWord(userInput)) {
            InputState.VALID -> {
                userInput = ""
                updateGrid()
                updateAlphabet()
            }
            InputState.NOT_IN_DICTIONARY -> {
                pushMessage("Not in word list")
                updateGrid()
            }
            InputState.TOO_SHORT -> {
                pushMessage("Not enough letters")
                updateGrid()
            }
            else -> Unit
        }

        updateEndOfGame()
        actionsEnabled = _actionsEnabled

        // save data and compute stats
        if (rules.state !is State.Playing && answer.isNotEmpty()) {
            repository.addRecord(
                WordleRecord(
                    Calendar.getInstance().time,
                    wordleId,
                    answer,
                    rules.state.maxTries,
                    rules.state.answers.map {
                        it.letters.concatToString()
                    }
                )
            )
            scope.launch(Dispatchers.Default) {
                repository.saveRecords()
            }

            statistics = repository.allRecords.stats()
        }

        // notify user
        if (victory && !oldVictory) {
            pushMessage(rules.state.message)
        }
        if (rules.state !is State.Playing) {
            scope.launch {
                // slightly delay letting player consume information update
                withContext(Dispatchers.Default) {
                    do {
                        delay(250)
                    } while (_userFeedback.isNotEmpty())
                }
                requestDialog(AppDialog.STATS_DIALOG)
            }
        }
    }

    fun restart() {
        val availableWords = availableWords
        if (availableWords.isEmpty()) {
            pushMessage("All known words were already played.")
            return
        }

        wordleId = -1
        // pick wordleId among full dictionary to keep stability
        while (wordleId !in availableWords.indices && availableWords.isNotEmpty()) {
            wordleId = dictionary.indices.random()
        }
        val rules = WordleRules(availableWords, availableWords[wordleId])
        this.rules = rules
        userInput = ""
        _userFeedback.clear()
        userFeedback = _userFeedback.toList()
        updateGrid()
        updateAlphabet()
        updateEndOfGame()
        actionsEnabled = _actionsEnabled
    }

    fun pushMessage(message: String) {
        _userFeedback += message
        userFeedback = _userFeedback.toList()
    }

    fun consumeMessage(message: String) {
        _userFeedback -= message
        userFeedback = _userFeedback.toList()
    }

    fun requestDialog(dialog: AppDialog) {
        // FIXME shouldn't we check for another one is already being displayed? priority? stack?
        requestedDialog = dialog
        actionsEnabled = _actionsEnabled
    }

    fun dismissDialog() {
        requestedDialog = null
        actionsEnabled = _actionsEnabled
    }
}
