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

import com.lordcodes.turtle.shellRun
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class OSName {
    UNKNOWN,
    WINDOWS,
    MAC,
    LINUX,
}

val OS: OSName
    get() {
        val os = System.getProperty("os.name").lowercase()
        return when {
            os.contains("mac") -> OSName.MAC
            os.contains("win") -> OSName.WINDOWS
            os.contains("nix") || os.contains("nux") || os.contains("aix") -> OSName.LINUX
            else -> OSName.UNKNOWN
        }
    }

suspend fun String.copyToClipboard(): Boolean {
    // TODO check for command availability and find fallbacks if possible
    val copyCommand = when (OS) {
        OSName.MAC -> "pbcopy"
        OSName.WINDOWS -> "clip" // in WSL2 there is clipcopy
        OSName.LINUX -> "xclip" // there is also xsel --clipboard --input
        else -> null
    }
    return if (copyCommand != null) {
        withContext(Dispatchers.IO) {
            try {
                // XXXcopy <<< "str"
                shellRun("/bin/sh", listOf("-c", "$copyCommand <<< \"${this@copyToClipboard}\""))
                true
            } catch (e: Exception) {
                false
            }
        }
    } else {
        false
    }
}
