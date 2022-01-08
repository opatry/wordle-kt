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

import androidx.compose.ui.graphics.Color

val green = Color(0xFF6AAA64)
val darkenGreen = Color(0xFF538D4E)
val yellow = Color(0xFFC9B458)
val darkenYellow = Color(0xFFB59F3B)
val lightGray = Color(0xFFD8D8D8)
val gray = Color(0xFF86888A)
val darkGray = Color(0xFF939598)
val white = Color(0xFFFFFFFF)
val black = Color(0xFF212121)
val orange = Color(0xFFf5793A) // used for high contrast mode
val blue = Color(0xFF85C0f9) // used for high contrast mode

val colorTone1 = Color(0xFF1A1A1B)
val colorTone2 = Color(0xFF787C7E)
val colorTone3 = Color(0xFF878A8C)
val colorTone4 = Color(0xFFD3D6DA)
val colorTone5 = Color(0xFFEDEFF1)
val colorTone6 = Color(0xFFF6f7F8)
val colorTone7 = Color(0xFFFFFFFF)

val colorPresent = yellow
val colorCorrect = green
val colorAbsent = colorTone2
val tileTextColor = colorTone7
val keyTextColor = colorTone1
val keyEvaluatedTextColor = colorTone7
val keyBg = colorTone4
val keyBgPresent = colorPresent
val keyBgCorrect = colorCorrect
val keyBgAbsent = colorAbsent