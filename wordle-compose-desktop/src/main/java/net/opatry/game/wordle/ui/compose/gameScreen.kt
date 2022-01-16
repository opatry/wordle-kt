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

package net.opatry.game.wordle.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.data.Settings
import net.opatry.game.wordle.data.WordleRecord
import net.opatry.game.wordle.ui.WordleViewModel
import net.opatry.game.wordle.ui.compose.component.Alphabet
import net.opatry.game.wordle.ui.compose.component.Dialog
import net.opatry.game.wordle.ui.compose.component.PopupOverlay
import net.opatry.game.wordle.ui.compose.component.WordleGrid
import net.opatry.game.wordle.ui.compose.theme.AppIcon
import net.opatry.game.wordle.ui.compose.theme.colorTone1
import net.opatry.game.wordle.ui.compose.theme.colorTone7
import net.opatry.game.wordle.ui.compose.theme.isHighContrastMode
import net.opatry.game.wordle.ui.compose.theme.painterResource

val AnswerFlag.toEmoji: String
    get() = when (this) {
        AnswerFlag.NONE -> "⬜"
        AnswerFlag.PRESENT -> if (isHighContrastMode) "🟦" else "🟨"
        AnswerFlag.ABSENT -> "⬛"
        AnswerFlag.CORRECT -> if (isHighContrastMode) "🟧" else "🟩"
    }

private fun StringBuffer.appendAsAnswer(word: String, selectedWord: String) {
    val answer = Answer.computeAnswer(word, selectedWord)
    append(
        answer.flags.joinToString(
            separator = " ",
            postfix = "\n",
            transform = AnswerFlag::toEmoji
        )
    ).trimEnd()
}

val WordleRecord?.isVictory: Boolean
    get() = this != null && answer == guesses.lastOrNull()

private val WordleRecord?.resultString: String
    get() {
        if (this == null) return ""

        val buffer = StringBuffer()
        if (isVictory) {
            buffer.append("Wordle $wordleId ${guesses.size}/$maxTries\n")
        } else {
            buffer.append("Wordle $wordleId X/$maxTries\n")
        }

        guesses.forEach { buffer.appendAsAnswer(it, answer) }

        return buffer.toString()
    }

@ExperimentalComposeUiApi
fun handleKey(viewModel: WordleViewModel, key: Key): Boolean {
    val userInput = viewModel.userInput
    when (key.keyCode) {
        in Key.A.keyCode..Key.Z.keyCode ->
            viewModel.updateUserInput(userInput + key.nativeKeyCode.toChar())
        Key.Backspace.keyCode ->
            if (userInput.isNotEmpty()) {
                viewModel.updateUserInput(userInput.dropLast(1))
            }
        Key.NumPadEnter.keyCode,
        Key.Enter.keyCode ->
            viewModel.validateUserInput()
        else -> return false
    }
    return true
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun GameScreen(settings: Settings, viewModel: WordleViewModel) {
    val userFeedback by rememberUpdatedState(viewModel.userFeedback)
    val showRulesDialog by rememberUpdatedState(viewModel.showRules)
    var showRulesPanel by remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }
    var showSettingsPanel by remember { mutableStateOf(false) }
    val dialogVisible = arrayOf(
        showRulesDialog,
        showRulesPanel,
        showStatsDialog,
        showSettingsPanel,
    ).any { it }
    val actionsEnabled = dialogVisible.not() && viewModel.grid.isNotEmpty() && viewModel.alphabet.isNotEmpty()

    val statistics by rememberUpdatedState(viewModel.statistics)

    LaunchedEffect(viewModel.victory) {
        // FIXME delay dialog appearance, otherwise causing a small freeze when transitioning from !victory to victory.
        //  Maybe conflicting animation for dialog appearance & toast?
        delay(100)
        showStatsDialog = viewModel.victory
    }

    val focusRequester = FocusRequester()
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        Modifier
            .fillMaxHeight()
            .width(400.dp)
            .padding(2.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .focusRequester(focusRequester)
                .focusable(true)
                .onKeyEvent { event ->
                    when {
                        event.type != KeyEventType.KeyUp -> false
                        dialogVisible && event.key == Key.Escape -> {
                            // FIXME a bit fragile if a new one is added
                            showRulesPanel = false
                            showStatsDialog = false
                            showSettingsPanel = false
                            viewModel.dismissRules()
                            true
                        }
                        actionsEnabled -> handleKey(viewModel, event.key)
                        else -> false
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Toolbar(
                enabled = actionsEnabled,
                onHowToClick = { showRulesPanel = true },
                onStatsClick = { showStatsDialog = true },
                onSettingsClick = { showSettingsPanel = true }
            )
            Divider()

            AnswerPlaceHolder(viewModel.answer, enabled = actionsEnabled, viewModel::restart)

            if (viewModel.grid.isNotEmpty() && viewModel.alphabet.isNotEmpty()) {
                WordleGrid(viewModel.grid)

                Spacer(Modifier.weight(1f))

                Alphabet(viewModel.alphabet, enabled = actionsEnabled) { key ->
                    handleKey(viewModel, key)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { handleKey(viewModel, Key.Backspace) }, enabled = actionsEnabled) {
                        Text("⌫")
                    }
                    Button(onClick = { handleKey(viewModel, Key.Enter) }, enabled = actionsEnabled) {
                        Text("Enter")
                    }
                }
            } else if (!viewModel.loading) {
                Text("No game data available", color = MaterialTheme.colors.error)
            }
        }

        Column(Modifier.padding(top = 80.dp)) {
            userFeedback.forEach { message ->
                Toast(message, Modifier.padding(bottom = 4.dp)) {
                    viewModel.consumed(message)
                }
            }
        }

        AnimatedVisibility(
            showRulesPanel,
            enter = fadeIn() + slideInVertically(),
            exit = slideOutVertically() + fadeOut()
        ) {
            PopupOverlay("How to play", onClose = { showRulesPanel = false }) {
                HowToPanel()
            }
        }

        AnimatedVisibility(
            showSettingsPanel,
            enter = fadeIn() + slideInVertically(),
            exit = slideOutVertically() + fadeOut()
        ) {
            PopupOverlay("Settings", onClose = { showSettingsPanel = false }) {
                SettingsPanel(settings)
            }
        }
    }

    AnimatedVisibility(
        showRulesDialog,
        enter = fadeIn() + slideInVertically(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Dialog(
            title = null,
            Modifier
                .size(width = 380.dp, height = 520.dp),
            onClose = { viewModel.dismissRules() }
        ) {
            HowToPanel()
        }
    }

    AnimatedVisibility(
        showStatsDialog,
        enter = fadeIn() + scaleIn(),
        exit = scaleOut() + fadeOut()
    ) {
        val lastRecord = viewModel.lastRecord
        Dialog(
            title = "Statistics",
            Modifier
                .size(width = 380.dp, 450.dp),
            onClose = { showStatsDialog = false }
        ) {
            val clipboard = LocalClipboardManager.current
            StatsPanel(statistics, lastRecord) {
                if (lastRecord.isVictory) {
                    val lastRecordString = lastRecord.resultString
                    clipboard.setText(AnnotatedString(lastRecordString))
                    // TODO how to display toast in combination of view model provided ones
                    //userFeedback += "Copied results to clipboard"
                }
            }
        }
    }
}

@Composable
fun Toast(label: String, modifier: Modifier = Modifier, onDismiss: (String) -> Unit) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(label) {
        delay(1000)
        visible = false
        delay(300)
        onDismiss(label)
    }

    AnimatedVisibility(
        visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Text(
            label,
            modifier
                .clip(RoundedCornerShape(4.dp))
                .background(colorTone1)
                .padding(8.dp),
            color = colorTone7,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun Toolbar(
    enabled: Boolean,
    onHowToClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onHowToClick, enabled = enabled) {
            Icon(painterResource(AppIcon.Help), "How to play")
        }

        Text("Wordle", Modifier.weight(1f), style = MaterialTheme.typography.h1)

        Row {
            IconButton(onClick = onStatsClick, enabled = enabled) {
                Icon(painterResource(AppIcon.Leaderboard), "Statistics")
            }

            IconButton(onClick = onSettingsClick, enabled = enabled) {
                Icon(painterResource(AppIcon.Settings), "Settings")
            }
        }
    }
}

@Composable
fun AnswerPlaceHolder(answer: String, enabled: Boolean = true, onRestart: () -> Unit) {
    val isAnswerVisible = answer.isNotEmpty()
    val animatedAlpha by animateFloatAsState(if (isAnswerVisible) 1f else 0f)
    // force 0 without animation when resetting from "xxx" to "" to avoid poor visual
    val alpha = if (isAnswerVisible) animatedAlpha else 0f

    Row(
        Modifier.alpha(alpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Box(
            Modifier
                .padding(2.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colors.primary)
                .padding(4.dp)
        ) {
            Text(
                answer,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h2
            )
        }

        IconButton(enabled = enabled, onClick = onRestart) {
            Icon(painterResource(AppIcon.Refresh), "Play again")
        }
    }
}
