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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.opatry.game.`wordle-compose-app`.generated.resources.Res
import net.opatry.game.`wordle-compose-app`.generated.resources.ic_open_in
import net.opatry.game.`wordle-compose-app`.generated.resources.setting_color_blind_desc
import net.opatry.game.`wordle-compose-app`.generated.resources.setting_color_blind_title
import net.opatry.game.`wordle-compose-app`.generated.resources.setting_dark_theme_title
import net.opatry.game.`wordle-compose-app`.generated.resources.setting_feedback_open_outside
import net.opatry.game.`wordle-compose-app`.generated.resources.setting_feedback_title
import net.opatry.game.`wordle-compose-app`.generated.resources.setting_hard_mode_desc
import net.opatry.game.`wordle-compose-app`.generated.resources.setting_hard_mode_title
import net.opatry.game.wordle.data.Settings
import net.opatry.game.wordle.ui.compose.theme.isHighContrastMode
import net.opatry.game.wordle.ui.compose.theme.isSystemInDarkTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsPanel(settings: Settings) {
    Column {
        Setting(stringResource(Res.string.setting_hard_mode_title), stringResource(Res.string.setting_hard_mode_desc)) {
            Switch(
                settings.hardMode,
                onCheckedChange = {},
                enabled = false
            )
        }
        Divider()
        Setting(stringResource(Res.string.setting_dark_theme_title)) {
            Switch(
                isSystemInDarkTheme,
                onCheckedChange = {
                    // TODO UI state should observe directly from settings
                    // TODO we should extract pure settings business logic from pure UI
                    settings.enableDarkMode(it)
                    isSystemInDarkTheme = it
                }
            )
        }
        Divider()
        Setting(stringResource(Res.string.setting_color_blind_title), stringResource(Res.string.setting_color_blind_desc)) {
            Switch(
                isHighContrastMode,
                onCheckedChange = {
                    // TODO UI state should observe directly from settings
                    // TODO we should extract pure settings business logic from pure UI
                    settings.enableHighContrastMode(it)
                    isHighContrastMode = it
                }
            )
        }
        Divider()
        Setting(label = stringResource(Res.string.setting_feedback_title)) {
            IconButton(onClick = { }, enabled = false) {
                Icon(painterResource(Res.drawable.ic_open_in), stringResource(Res.string.setting_feedback_open_outside))
            }
        }
        Divider()

        Spacer(Modifier.height(24.dp))

        Column(Modifier.padding(vertical = 4.dp)) {
            Hint("#203") // TODO word ID
            Hint("db1931a8") // TODO player ID
        }
    }
}

@Composable
private fun Hint(label: String) {
    Text(label, Modifier.fillMaxWidth(), style = MaterialTheme.typography.overline, textAlign = TextAlign.End)
}

@Composable
private fun Setting(
    label: String,
    description: String? = null,
    actionContent: @Composable () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingLabel(
            label,
            Modifier.weight(1f),
            description = description
        )
        actionContent()
    }
}

@Composable
private fun SettingLabel(label: String, modifier: Modifier = Modifier, description: String? = null) {
    Column(modifier, Arrangement.spacedBy(2.dp, Alignment.CenterVertically)) {
        Text(label, Modifier.fillMaxWidth(), style = MaterialTheme.typography.subtitle1)
        if (!description.isNullOrBlank()) {
            Text(
                description,
                Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.caption
            )
        }
    }
}
