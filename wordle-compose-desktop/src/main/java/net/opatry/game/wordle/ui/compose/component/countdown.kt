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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import java.util.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime


@ExperimentalTime
@Composable
fun NextWordleCountDown(modifier: Modifier = Modifier) {
    var now by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    val tomorrow = with(Calendar.getInstance()) {
        timeInMillis = now
        set(Calendar.HOUR, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 1)
        // tomorrow
        add(Calendar.DATE, 1)
        timeInMillis
    }
    LaunchedEffect(now) {
        // delay at most 1 second
        delay(1000L - (System.currentTimeMillis() - now).coerceAtLeast(0))
        now = Calendar.getInstance().timeInMillis
    }
    val diff = (tomorrow - now).milliseconds
    diff.coerceAtLeast(1.milliseconds).toComponents { hours, minutes, seconds, _ ->
        Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Next Wordle", style = MaterialTheme.typography.h3)
            CountDownLabel(hours, minutes, seconds)
        }
    }
}

@Composable
fun CountDownLabel(hours: Long, minutes: Int, seconds: Int) {
    fun Number.pad0(length: Int = 2) = toString().padStart(length, '0')

    Text(
        "${hours.pad0()}:${minutes.pad0()}:${seconds.pad0()}",
        style = MaterialTheme.typography.h1
    )
}
