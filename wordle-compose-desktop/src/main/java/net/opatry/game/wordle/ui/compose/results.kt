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

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.opatry.game.wordle.WordleStats
import net.opatry.game.wordle.ui.compose.theme.AppIcon
import net.opatry.game.wordle.ui.compose.theme.colorTone3
import net.opatry.game.wordle.ui.compose.theme.colorTone7
import net.opatry.game.wordle.ui.compose.theme.isHighContrastMode
import net.opatry.game.wordle.ui.compose.theme.painterResource

@Composable
fun StatsPanel(stats: WordleStats, highlightLastScore: Boolean = true) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        StatCells(stats)
        val distribution = stats.victoryDistribution
        val lastScoreIndex = stats.lastScore - 1
        val highlightedIndex = when {
            highlightLastScore && lastScoreIndex in distribution.indices -> lastScoreIndex
            else -> -1
        }
        VictoryDistribution(stats.playedCount, stats.victoryDistribution, highlightedIndex)
    }
}

@Composable
fun StatCells(stats: WordleStats) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        StatCell("Played", stats.playedCount)
        StatCell("Win %", (stats.victoryRatio * 100).toInt())
        StatCell("Current\nStreak", stats.currentStreak)
        StatCell("Max\nStreak", stats.bestStreak)
    }
}

@Composable
fun StatCell(label: String, value: Int, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value.toString(),
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Center
        )
        Text(
            label,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun VictoryDistribution(playedCount: Int, victoryDistribution: IntArray, highlightedIndex: Int) {
    Text(
        "Guess Distribution",
        Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.h3
    )

    val max = victoryDistribution.maxOrNull()
    if (max == null || playedCount == 0) {
        Text("No Data", Modifier.padding(8.dp))
    } else {
        Column(
            Modifier.padding(horizontal = 24.dp, vertical = 8.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            victoryDistribution.forEachIndexed { index, count ->
                val ratio = if (max > 0) count / max.toFloat() else 0f
                StatProgress((index + 1).toString(), count, ratio, index == highlightedIndex)
            }
        }
    }
}

@Composable
fun StatProgress(label: String, value: Int, ratio: Float, isHighlighted: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            label,
            Modifier.width(24.dp),
            textAlign = TextAlign.End
        )
        Box(contentAlignment = Alignment.CenterEnd) {
            // FIXME little hack:
            //  Progress is always 100% but laid out to honor given ratio.
            //  This is done to put the progress label over the progress bar right aligned.
            //  Coerce at least to make "0" visible.
            LinearProgressIndicator(
                1f,
                Modifier.fillMaxWidth(ratio.coerceAtLeast(.06f)).height(24.dp),
                backgroundColor = colorTone7,
                color = if (isHighlighted) MaterialTheme.colors.primary else colorTone3
            )
            Text(
                value.toString(),
                Modifier.padding(horizontal = 4.dp),
                fontWeight = FontWeight.Bold,
                color = colorTone7
            )
        }
    }
}

@Composable
fun ResultsPanel(stats: WordleStats, resultLabel: String, onShare: () -> Unit) {
    val label = if (isHighContrastMode) {
        resultLabel
            .replace("🟨", "🟦")
            .replace("🟩", "🟧")
    } else {
        resultLabel
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatsPanel(stats)

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)) {
            Text(label, textAlign = TextAlign.Center)

            Button(onClick = onShare, Modifier.padding(top = 8.dp)) {
                Text("Share")
                Spacer(Modifier.width(8.dp))
                Icon(painterResource(AppIcon.Share), null)
            }
        }
    }
}
