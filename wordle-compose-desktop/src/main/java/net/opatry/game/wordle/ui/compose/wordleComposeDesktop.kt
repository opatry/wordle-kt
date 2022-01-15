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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import net.opatry.game.wordle.ui.compose.theme.AppIcon
import net.opatry.game.wordle.ui.compose.theme.IconProvider
import net.opatry.game.wordle.ui.compose.theme.LocalIconProvider

object DesktopIconProvider : IconProvider {
    @Composable
    override fun providePainter(icon: AppIcon): Painter {
        val resourcePath = when (icon) {
            AppIcon.Open -> "ic_open_in.xml"
            AppIcon.Help -> "ic_help_outline.xml"
            AppIcon.Settings -> "ic_settings_outline.xml"
            AppIcon.Refresh -> "ic_refresh.xml"
            AppIcon.Close -> "ic_close.xml"
            AppIcon.Share -> "ic_share_outline.xml"
            AppIcon.Leaderboard -> "ic_leaderboard_outline.xml"
        }
        return painterResource(resourcePath)
    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun main() {
    singleWindowApplication(
        title = "Wordle Compose",
        // FIXME can't make iconFile work
        //  Icon in the titlebar of the window (for platforms which support this).
        //  On macOS individual windows can't have a separate icon.
        //  To change the icon in the Dock, set it via `iconFile` in `build.gradle`
        icon = BitmapPainter(useResource("icon.png", ::loadImageBitmap)),
        state = WindowState(
            position = WindowPosition(Alignment.Center),
            width = 500.dp,
            height = 700.dp
        ),
        // TODO how to restrict to a minimum width & height (500x700?)
        resizable = true,
    ) {
        CompositionLocalProvider(LocalIconProvider provides DesktopIconProvider) {
            WordleApp()
        }
    }
}
