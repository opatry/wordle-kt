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

package net.opatry.game.wordle.ui.compose.theme


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


var isSystemInDarkTheme by mutableStateOf(false)
var isHighContrastMode by mutableStateOf(false)

private fun accentColor(highContrastMode: Boolean): Color = if (highContrastMode) orange else green
private fun accentColorVariant(highContrastMode: Boolean): Color = if (highContrastMode) orange else darkenGreen

private fun lightColorPalette(highContrastMode: Boolean): Colors {
    val accentColor = accentColor(highContrastMode)
    val accentColorVariant = accentColorVariant(highContrastMode)
    return lightColors(
        primary = accentColor,
        primaryVariant = accentColorVariant,
        secondary = accentColor,
        secondaryVariant = accentColorVariant,
        background = colorTone7,
        surface = colorTone7,
        onPrimary = white,
        onBackground = colorTone1,
        onSurface = colorTone1,
    )
}

private fun darkColorPalette(highContrastMode: Boolean): Colors {
    val accentColor = accentColor(highContrastMode)
    val accentColorVariant = accentColorVariant(highContrastMode)
    return darkColors(
        primary = accentColor,
        primaryVariant = accentColorVariant,
        secondary = accentColor,
        secondaryVariant = accentColorVariant,
        background = colorTone7,
        surface = colorTone7,
        onPrimary = white,
        onBackground = colorTone1,
        onSurface = colorTone1,
    )
}

@Composable
fun WordleComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme,
    highContrastMode: Boolean = isHighContrastMode,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) darkColorPalette(highContrastMode) else lightColorPalette(highContrastMode),
        typography = typography,
        shapes = Shapes(small = RoundedCornerShape(4.dp), medium = RoundedCornerShape(4.dp)),
        content = {
            Surface(
                Modifier.fillMaxSize(),
                color = MaterialTheme.colors.surface
            ) {
                content()
            }
        }
    )
}
