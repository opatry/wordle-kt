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

package net.opatry.game.wordle.data

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class SettingsData(
    val hardMode: Boolean = false,
    val darkMode: Boolean = false,
    val highContrastMode: Boolean = false,
)

// TODO would be better to abstract data source
// TODO thread safety
class Settings(private val data: File) {
    private val gson = GsonBuilder().create()

    private var settings = SettingsData()

    val hardMode: Boolean
        get() = settings.hardMode
    val darkMode: Boolean
        get() = settings.darkMode
    val highContrastMode: Boolean
        get() = settings.highContrastMode

    init {
        data.parentFile?.mkdirs()
        GlobalScope.launch {
            load()
        }
    }

    private suspend fun load() {
        if (!data.isFile) return

        settings = withContext(Dispatchers.IO) {
            data.bufferedReader().use { reader ->
                gson.fromJson(
                    reader,
                    object : TypeToken<SettingsData>() {}.type
                )
            }
        }
    }

    private suspend fun save() {
        withContext(Dispatchers.IO) {
            data.writer().use { writer ->
                gson.toJson(settings, writer)
            }
        }
    }

    private fun persist(updateLogic: () -> Unit) {
        GlobalScope.launch {
            updateLogic()
            save()
        }
    }

    fun enableHardMode(enabled: Boolean) {
        persist {
            settings = settings.copy(hardMode = enabled)
        }
    }

    fun enableDarkMode(enabled: Boolean) {
        persist {
            settings = settings.copy(darkMode = enabled)
        }
    }

    fun enableHighContrastMode(enabled: Boolean) {
        persist {
            settings = settings.copy(highContrastMode = enabled)
        }
    }
}