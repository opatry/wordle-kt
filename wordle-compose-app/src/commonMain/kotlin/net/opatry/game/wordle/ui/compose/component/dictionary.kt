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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.opatry.game.`wordle-compose-app`.generated.resources.Res
import net.opatry.game.`wordle-compose-app`.generated.resources.dictionary_choose_title
import net.opatry.game.`wordle-compose-app`.generated.resources.dictionary_select_button
import net.opatry.game.`wordle-compose-app`.generated.resources.dictionary_word_size
import net.opatry.game.`wordle-compose-app`.generated.resources.dictionary_word_size_with_qualifier
import net.opatry.game.wordle.Dictionary
import org.jetbrains.compose.resources.stringResource
import java.util.*


@ExperimentalFoundationApi
@Composable
fun DictionaryPicker(dictionaries: List<Dictionary>, onSelect: (Dictionary?) -> Unit) {
    if (dictionaries.size <= 1) {
        onSelect(dictionaries.firstOrNull())
        return
    }

    // TODO define a preselected one?
    //  1. last used if any
    //  2. find current locale with 5 letters
    //  3. none

    var selectedDictionary by remember { mutableStateOf<Dictionary?>(null) }
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            stringResource(Res.string.dictionary_choose_title),
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            style = MaterialTheme.typography.h1
        )
        LazyColumn(Modifier.weight(1f, false)) {
            val groups = dictionaries.groupBy(Dictionary::language)
            groups.forEach { (language, dictionaries) ->
                stickyHeader {
                    LanguageRow(
                        language,
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                    )
                }
                items(dictionaries) { dictionary ->
                    WordSizeRow(dictionary.wordSize, dictionary.qualifier, dictionary == selectedDictionary) {
                        selectedDictionary = dictionary
                    }
                }
            }
        }
        Button(
            onClick = {
                val dictionary = selectedDictionary
                if (dictionary != null) {
                    onSelect(dictionary)
                }
            },
            enabled = selectedDictionary != null
        ) {
            Text(stringResource(Res.string.dictionary_select_button))
        }
    }
}

@Composable
fun LanguageRow(language: String, modifier: Modifier = Modifier) {
    val locale = Locale(language)
    Row(
        modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            locale.displayLanguage.replaceFirstChar(Char::uppercaseChar),
            style = MaterialTheme.typography.h3
        )
    }
}

@Composable
private fun WordSizeRow(wordSize: Int, qualifier: String, isSelected: Boolean, onSelect: () -> Unit) {
    val (foregroundColor, backgroundColor) = when (isSelected) {
        true -> MaterialTheme.colors.onPrimary to MaterialTheme.colors.primary
        else -> LocalContentColor.current to Color.Transparent
    }

    Row(
        Modifier
            .padding(vertical = 4.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onSelect)
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val label = if (qualifier.isNotBlank())
            stringResource(Res.string.dictionary_word_size_with_qualifier, wordSize, qualifier)
        else
            stringResource(Res.string.dictionary_word_size, wordSize)
        Text(
            label,
            color = foregroundColor
        )
    }
}
