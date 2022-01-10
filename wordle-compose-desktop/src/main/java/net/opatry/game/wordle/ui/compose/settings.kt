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

import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import net.opatry.game.wordle.ui.compose.theme.isHighContrastMode
import net.opatry.game.wordle.ui.compose.theme.isSystemInDarkTheme

@Composable
fun SettingsPanel() {
    Column(
        Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        Setting("Hard mode", "Any revealed hints must be used in subsequent guesses") {
            Switch(false, onCheckedChange = { }, enabled = false)
        }
        Divider()
        Setting("Dark Theme") {
            Switch(isSystemInDarkTheme, onCheckedChange = { isSystemInDarkTheme = it })
        }
        Divider()
        Setting("Color Blind Mode", "High contrast colors") {
            Switch(isHighContrastMode, onCheckedChange = { isHighContrastMode = it })
        }
        Divider()
        Setting(label = "Feedback") {
            IconButton(onClick = { }, enabled = false) {
                Icon(painterResource("ic_open_in.xml"), "Open outside to provide feedback")
            }
        }
        Divider()

        Spacer(Modifier.weight(1f))

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
    Column(modifier, Arrangement.SpaceAround) {
        Text(label, Modifier.fillMaxWidth(), style = MaterialTheme.typography.subtitle1)
        if (!description.isNullOrBlank()) {
            Text(
                description,
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                style = MaterialTheme.typography.caption
            )
        }
    }
}
