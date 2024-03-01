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

package net.opatry.game.wordle

import net.opatry.game.wordle.data.WordleRecord
import kotlin.math.max

data class WordleStats(
    val playedCount: Int,
    val victoryDistribution: IntArray,
    val lastScore: Int, // 0 means lost game
    val currentStreak: Int,
    val bestStreak: Int,
) {
    val victoryCount: Int = victoryDistribution.sum()
    val victoryRatio: Float = if (playedCount > 0) victoryCount / playedCount.toFloat() else 0f

    init {
        require(victoryDistribution.size == 6) { "Invalid victory distribution" }
        require(victoryCount <= playedCount) { "There are more victories than played games" }
        if (lastScore > 0) {
            require(victoryDistribution[lastScore - 1] > 0) { "Last score not in victory distribution" }
        }
        require(bestStreak >= currentStreak) { "Best streak is lower than current one" }
        require(bestStreak <= victoryCount) { "Best streal exceeds victory count" }
    }
}

val WordleRecord.score: Int
    get() = when {
        guesses.lastOrNull() == answer -> guesses.size
        else -> 0
    }

fun List<WordleRecord>.stats(): WordleStats {
    var playedCount = 0
    val victoryDistribution = IntArray(6) { 0 }
    var lastScore = 0
    var currentStreak = 0
    var bestStreak = 0
    this.forEach { record ->
        ++playedCount
        // a score of 0 means defeat, 0 - 1 won't be a valid index and will be skipped
        // the score is the number of count needed to find the answer, 1 is the best
        lastScore = record.score
        val scoreIndex = lastScore - 1
        if (scoreIndex in victoryDistribution.indices) {
            ++currentStreak
            ++victoryDistribution[scoreIndex]
        } else {
            currentStreak = 0
        }
        bestStreak = max(currentStreak, bestStreak)
    }
    return WordleStats(playedCount, victoryDistribution, lastScore, currentStreak, bestStreak)
}