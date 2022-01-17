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

package net.opatry.game.wordle.ui.compose.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.opatry.game.wordle.ui.compose.theme.colorTone1
import net.opatry.game.wordle.ui.compose.theme.colorTone7


@Composable
fun AutoDismissToast(label: String, modifier: Modifier = Modifier, onDismiss: (String) -> Unit) {
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
        Toast(label, modifier)
    }
}

@Composable
fun Toast(label: String, modifier: Modifier = Modifier) {
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
