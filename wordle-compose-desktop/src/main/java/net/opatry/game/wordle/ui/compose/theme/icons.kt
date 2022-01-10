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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.painter.Painter


enum class AppIcon {
    Open,
    Help,
    Settings,
    Refresh,
    Close,
    Share,
}

interface IconProvider {
    @Composable
    fun providePainter(icon: AppIcon): Painter
}

private object PlaceHolderIconProvider : IconProvider {
    @Composable
    override fun providePainter(icon: AppIcon): Painter {
        throw IllegalStateException(
            """
            A custom LocalIconProvider must be set prior to use (context: $icon)
            
            object CustomIconProvider : IconProvider {
                @Composable
                override fun providePainter(icon: AppIcon): Painter {
                    return when (icon) {
                        ...
                        AppIcon.XXX -> custom painter resource logic
                    }
                }
            }
            CompositionLocalProvider(LocalIconProvider provides CustomIconProvider) {
                MyApp()
            }
            """.trimIndent()
        )
    }
}

// should be defined early in the app lifecycle
val LocalIconProvider = staticCompositionLocalOf<IconProvider> { PlaceHolderIconProvider }

@Composable
fun painterResource(icon: AppIcon): Painter = LocalIconProvider.current.providePainter(icon)