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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.opatry.game.wordle.ui.WordleViewModel
import net.opatry.game.wordle.ui.compose.component.Alphabet
import net.opatry.game.wordle.ui.compose.component.Dialog
import net.opatry.game.wordle.ui.compose.component.PopupOverlay
import net.opatry.game.wordle.ui.compose.component.WordleGrid
import net.opatry.game.wordle.ui.compose.theme.AppIcon
import net.opatry.game.wordle.ui.compose.theme.colorTone1
import net.opatry.game.wordle.ui.compose.theme.colorTone7
import net.opatry.game.wordle.ui.compose.theme.painterResource

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
fun GameScreen(viewModel: WordleViewModel) {
    val userFeedback by rememberUpdatedState(viewModel.userFeedback)
    val showFirstLaunchSheet by rememberUpdatedState(viewModel.firstLaunch)
    var showHowTo by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showResultsDialog by remember { mutableStateOf(false) }
    val actionsEnabled = arrayOf(
        showFirstLaunchSheet,
        showHowTo,
        showStats,
        showSettings,
        showResultsDialog
    ).none { it }

    // TODO retrieve from ViewModel
    val stats = WordleStats(12, intArrayOf(0, 0, 1, 2, 3, 0), 3, 1, 1)

    LaunchedEffect(viewModel.victory) {
        // FIXME this causes a small freeze when transitioning from !victory to victory
        showResultsDialog = viewModel.victory
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
        // TODO Scaffold?
        Column(
            Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .focusable(true)
                .onKeyEvent { event ->
                    if (event.type != KeyEventType.KeyUp) {
                        return@onKeyEvent false
                    }
                    handleKey(viewModel, event.key)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Toolbar(
                enabled = actionsEnabled,
                onHowToClick = { showHowTo = true },
                onStatsClick = { showStats = true },
                onSettingsClick = { showSettings = true }
            )
            Divider()
            AnswerPlaceHolder(viewModel.answer, viewModel::restart)
            WordleGrid(viewModel.grid)
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
        }

        Column(Modifier.padding(top = 80.dp)) {
            userFeedback.forEach { message ->
                Toast(message, Modifier.padding(bottom = 4.dp)) {
                    viewModel.consumed(message)
                }
            }
        }

        AnimatedVisibility(
            showHowTo,
            enter = fadeIn() + slideInVertically(),
            exit = slideOutVertically() + fadeOut()
        ) {
            PopupOverlay("How to play", onClose = { showHowTo = false }) {
                HowToPanel()
            }
        }

        AnimatedVisibility(
            showFirstLaunchSheet,
            enter = fadeIn() + slideInVertically(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Dialog(
                title = null,
                Modifier
                    .size(width = 380.dp, height = 520.dp)
                    .padding(top = 50.dp),
                onClose = { viewModel.firstLaunchDone() }
            ) {
                HowToPanel()
            }
        }

        AnimatedVisibility(
            showStats,
            enter = fadeIn() + slideInVertically(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Dialog(
                title = "Statistics",
                Modifier
                    .size(width = 380.dp, height = 460.dp)
                    .padding(top = 50.dp),
                onClose = { showStats = false }
            ) {
                StatsPanel(stats)
            }
        }

        AnimatedVisibility(
            showSettings,
            enter = fadeIn() + slideInVertically(),
            exit = slideOutVertically() + fadeOut()
        ) {
            PopupOverlay("Settings", onClose = { showSettings = false }) {
                SettingsPanel()
            }
        }

        AnimatedVisibility(
            showResultsDialog,
            enter = fadeIn() + scaleIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Dialog(
                title = "Statistics",
                Modifier
                    .size(width = 380.dp, height = 620.dp)
                    .padding(top = 50.dp),
                onClose = { showResultsDialog = false }
            ) {
                ResultsPanel(stats, viewModel.stateLabel) { }
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
fun AnswerPlaceHolder(answer: String, onRestart: () -> Unit) {
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

        IconButton(onClick = onRestart) {
            Icon(painterResource(AppIcon.Refresh), "Play again")
        }
    }
}
