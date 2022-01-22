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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import net.opatry.game.wordle.Dictionary
import net.opatry.game.wordle.R
import net.opatry.game.wordle.allDictionaries
import net.opatry.game.wordle.data.Settings
import net.opatry.game.wordle.ui.compose.theme.AppIcon
import net.opatry.game.wordle.ui.compose.theme.IconProvider
import net.opatry.game.wordle.ui.compose.theme.LocalIconProvider
import java.io.File
import kotlin.time.ExperimentalTime


object AndroidIconProvider : IconProvider {
    @Composable
    override fun providePainter(icon: AppIcon): Painter {
        val drawableRes = when (icon) {
            AppIcon.Launcher -> R.drawable.ic_close // TODO R.mipmap.ic_launcher
            AppIcon.Open -> R.drawable.ic_open_in
            AppIcon.Help -> R.drawable.ic_help_outline
            AppIcon.Settings -> R.drawable.ic_settings_outline
            AppIcon.Refresh -> R.drawable.ic_refresh
            AppIcon.Close -> R.drawable.ic_close
            AppIcon.Back -> R.drawable.ic_back
            AppIcon.Share -> R.drawable.ic_share_outline
            AppIcon.Leaderboard -> R.drawable.ic_leaderboard_outline
        }
        return painterResource(drawableRes)
    }
}

@ExperimentalTime
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO inject
        val dataFile = File(filesDir, "records.json")
        val settingsFile = File(filesDir, "settings.json")

        // TODO inject
        val settings = Settings(settingsFile)

        // TODO inject
        val validDictionaries = allDictionaries
            .filter { it.wordSize in 4..8 }
            .sortedWith(compareBy(Dictionary::language, Dictionary::wordSize))

        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ProvideWindowInsets {
                CompositionLocalProvider(LocalIconProvider provides AndroidIconProvider) {
                    Box(
                        Modifier
                            .background(MaterialTheme.colors.surface)
                            .padding(
                                rememberInsetsPaddingValues(
                                    insets = LocalWindowInsets.current.systemBars,
                                    applyTop = true,
                                    applyBottom = true,
                                )
                            )
                    ) {
                        WordleApp(settings, dataFile, validDictionaries)
                    }
                }
            }
        }
    }
}
