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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import net.opatry.game.wordle.Answer
import net.opatry.game.wordle.AnswerFlag
import net.opatry.game.wordle.ui.compose.component.WordleWordRow

private fun embolden(word: String, sentence: String): AnnotatedString {
    // FIXME using space as word boundaries falls short but is enough for this screen for now.
    val index = sentence.indexOf(" $word ")
    return if (index != -1) {
        buildAnnotatedString {
            append(sentence.substring(0, index + 1))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(word)
            }
            append(sentence.substring(index + word.length + 1))
        }
    } else {
        AnnotatedString(sentence)
    }
}

@Composable
fun HowToPanel() {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Paragraph(embolden("WORDLE", "Guess the WORDLE in 6 tries."))
        Paragraph("After each guess, the color of the tiles will change to show how close your guess was to the word.")
        Divider()

        WordleSample(
            "WEARY",
            'W' to AnswerFlag.CORRECT,
            "The letter W is in the word and in the correct spot."
        )
        WordleSample(
            "PILOT",
            'L' to AnswerFlag.PRESENT,
            "The letter L is in the word but in the wrong spot."
        )
        WordleSample(
            "VAGUE",
            'U' to AnswerFlag.ABSENT,
            "The letter U is not in the word in any spot."
        )

        Divider()
        Paragraph(
            "A new WORDLE will be available each day!",
            style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun Paragraph(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.body2) {
    Paragraph(text = AnnotatedString(text), modifier = modifier.padding(vertical = 8.dp), style = style)
}

@Composable
private fun Paragraph(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.body2) {
    Text(text = text, modifier = modifier.padding(vertical = 8.dp), style = style)
}

@Composable
private fun WordleSample(word: String, emphasizeLetter: Pair<Char, AnswerFlag>, explanation: String) {
    val flags = Array(5) { AnswerFlag.NONE }
    val (letter, flag) = emphasizeLetter
    val emphasizeIndex = word.indexOf(letter)
    if (emphasizeIndex in flags.indices) {
        flags[emphasizeIndex] = flag
    }

    WordleWordRow(
        Answer(
            word.toCharArray(),
            flags
        ),
        Modifier.padding(vertical = 4.dp)
    )

    Paragraph(
        embolden(letter.toString(), explanation),
        style = MaterialTheme.typography.body2
    )
}
