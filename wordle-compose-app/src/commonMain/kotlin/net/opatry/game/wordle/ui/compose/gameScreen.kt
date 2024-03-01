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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import net.opatry.game.`wordle-compose-app`.generated.resources.Res
import net.opatry.game.`wordle-compose-app`.generated.resources.how_to_play_dialog_title
import net.opatry.game.`wordle-compose-app`.generated.resources.ic_back
import net.opatry.game.`wordle-compose-app`.generated.resources.ic_help_outline
import net.opatry.game.`wordle-compose-app`.generated.resources.ic_leaderboard_outline
import net.opatry.game.`wordle-compose-app`.generated.resources.ic_refresh
import net.opatry.game.`wordle-compose-app`.generated.resources.ic_settings_outline
import net.opatry.game.`wordle-compose-app`.generated.resources.main_nav_back_to_dictionaries
import net.opatry.game.`wordle-compose-app`.generated.resources.main_nav_how_to_play
import net.opatry.game.`wordle-compose-app`.generated.resources.main_nav_settings
import net.opatry.game.`wordle-compose-app`.generated.resources.main_nav_statistics
import net.opatry.game.`wordle-compose-app`.generated.resources.no_data_available
import net.opatry.game.`wordle-compose-app`.generated.resources.play_again
import net.opatry.game.`wordle-compose-app`.generated.resources.settings_dialog_title
import net.opatry.game.`wordle-compose-app`.generated.resources.statistics_dialog_title
import net.opatry.game.`wordle-compose-app`.generated.resources.stats_dialog_copy_notification
import net.opatry.game.`wordle-compose-app`.generated.resources.wordle_title
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.data.Settings
import net.opatry.game.wordle.data.WordleRecord
import net.opatry.game.wordle.ui.AppDialog
import net.opatry.game.wordle.ui.WordleViewModel
import net.opatry.game.wordle.ui.compose.component.Alphabet
import net.opatry.game.wordle.ui.compose.component.AutoDismissToast
import net.opatry.game.wordle.ui.compose.component.Dialog
import net.opatry.game.wordle.ui.compose.component.PopupOverlay
import net.opatry.game.wordle.ui.compose.component.Toast
import net.opatry.game.wordle.ui.compose.component.WordleGrid
import net.opatry.game.wordle.ui.compose.theme.isHighContrastMode
import net.opatry.game.wordle.ui.compose.theme.isSystemInDarkTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime

val AnswerFlag.toEmoji: String
    get() = when (this) {
        AnswerFlag.NONE -> if (isSystemInDarkTheme) "⬜" else "⬛"
        AnswerFlag.PRESENT -> if (isHighContrastMode) "🟦" else "🟨"
        AnswerFlag.ABSENT -> if (isSystemInDarkTheme) "⬛" else "⬜"
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
        // TODO string resources
        if (isVictory) {
            buffer.append("Wordle $wordleId ${guesses.size}/$maxTries\n")
        } else {
            buffer.append("Wordle $wordleId X/$maxTries\n")
        }

        guesses.forEach { buffer.appendAsAnswer(it, answer) }

        return buffer.toString()
    }

fun WordleViewModel.handleKey(letter: Char): Boolean {
    when (letter.uppercaseChar()) {
        in 'A'..'Z' ->
            updateUserInput(userInput + letter)
        '\b' ->
            if (userInput.isNotEmpty()) {
                updateUserInput(userInput.dropLast(1))
            }
        '\n' ->
            validateUserInput()
        else -> return false
    }
    return true
}

@ExperimentalTime
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun GameScreen(settings: Settings, viewModel: WordleViewModel, onClose: () -> Unit) {
    val userFeedback by rememberUpdatedState(viewModel.userFeedback)
    val requestedDialog by rememberUpdatedState(viewModel.requestedDialog)
    val actionsEnabled by rememberUpdatedState(viewModel.actionsEnabled)
    val statistics by rememberUpdatedState(viewModel.statistics)
    val grid by rememberUpdatedState(viewModel.grid)
    val alphabet by rememberUpdatedState(viewModel.alphabet)

    val focusRequester = FocusRequester()
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        Modifier.fillMaxSize(),
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
                        requestedDialog != null && event.key == Key.Escape -> {
                            viewModel.dismissDialog()
                            true
                        }
                        actionsEnabled -> viewModel.handleKey(event.utf16CodePoint.toChar())
                        else -> false
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GameToolbar(
                enabled = actionsEnabled,
                onBackClick = onClose,
                onHowToClick = { viewModel.requestDialog(AppDialog.HOWTO_PANEL) },
                onStatsClick = { viewModel.requestDialog(AppDialog.STATS_DIALOG) },
                onSettingsClick = { viewModel.requestDialog(AppDialog.SETTINGS_PANEL) }
            )
            Divider()

            EndOfGameControl(
                label = viewModel.endOfGameAnswer,
                canRestart = viewModel.canRestart,
                enabled = actionsEnabled,
                viewModel::restart
            )

            // FIXME "complex" decision to move at ViewModel level
            if (grid.isNotEmpty() && alphabet.isNotEmpty()) {
                WordleGrid(grid)

                Spacer(Modifier.weight(1f))

                Alphabet(alphabet, enabled = actionsEnabled) { letter ->
                    viewModel.handleKey(letter)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { viewModel.handleKey('\b') }, enabled = actionsEnabled) {
                        Text("⌫")
                    }
                    Button(onClick = { viewModel.handleKey('\n') }, enabled = actionsEnabled) {
                        Text("Enter")
                    }
                }
            } else if (!viewModel.loading) {
                Text(stringResource(Res.string.no_data_available), color = MaterialTheme.colors.error)
            }
        }

        PopupOverlay(
            visible = requestedDialog == AppDialog.HOWTO_PANEL,
            title = stringResource(Res.string.how_to_play_dialog_title),
            onClose = viewModel::dismissDialog
        ) {
            HowToPanel()
        }

        PopupOverlay(
            visible = requestedDialog == AppDialog.SETTINGS_PANEL,
            title = stringResource(Res.string.settings_dialog_title),
            onClose = viewModel::dismissDialog
        ) {
            SettingsPanel(settings)
        }
    }

    Dialog(
        visible = requestedDialog == AppDialog.HOWTO_DIALOG,
        title = null,
        Modifier.width(380.dp),
        onClose = viewModel::dismissDialog
    ) {
        HowToPanel()
    }

    Dialog(
        visible = requestedDialog == AppDialog.STATS_DIALOG,
        title = stringResource(Res.string.statistics_dialog_title),
        Modifier.width(380.dp),
        onClose = viewModel::dismissDialog
    ) {
        val lastRecord = viewModel.lastRecord
        val clipboard = LocalClipboardManager.current
        val notif = stringResource(Res.string.stats_dialog_copy_notification)
        StatsPanel(statistics, lastRecord) {
            val lastRecordString = lastRecord.resultString
            clipboard.setText(AnnotatedString(lastRecordString))
            viewModel.pushMessage(notif)
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        userFeedback.forEach { message ->
            AutoDismissToast(message, Modifier.padding(bottom = 4.dp), viewModel::consumeMessage)
        }
    }
}

@Composable
fun GameToolbar(
    enabled: Boolean,
    onBackClick: () -> Unit,
    onHowToClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            // FIXME either on close/back icon (where?) calling onClose() or settings -> dictionaries calling onDictionaryPickerRequest()?
            IconButton(onClick = onBackClick, enabled = enabled) {
                Icon(painterResource(Res.drawable.ic_back), stringResource(Res.string.main_nav_back_to_dictionaries))
            }
            IconButton(onClick = onHowToClick, enabled = enabled) {
                Icon(painterResource(Res.drawable.ic_help_outline), stringResource(Res.string.main_nav_how_to_play))
            }
        }

        Text(stringResource(Res.string.wordle_title), Modifier.weight(1f), style = MaterialTheme.typography.h1)

        Row {
            IconButton(onClick = onStatsClick, enabled = enabled) {
                Icon(painterResource(Res.drawable.ic_leaderboard_outline), stringResource(Res.string.main_nav_statistics))
            }

            IconButton(onClick = onSettingsClick, enabled = enabled) {
                Icon(painterResource(Res.drawable.ic_settings_outline), stringResource(Res.string.main_nav_settings))
            }
        }
    }
}

@Composable
fun EndOfGameControl(label: String, canRestart: Boolean, enabled: Boolean = true, onRestart: () -> Unit) {
    Row(
        Modifier.height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        if (label.isNotEmpty()) {
            Toast(label)
        }

        if (canRestart) {
            IconButton(enabled = enabled, onClick = onRestart) {
                Icon(painterResource(Res.drawable.ic_refresh), stringResource(Res.string.play_again))
            }
        }
    }
}
