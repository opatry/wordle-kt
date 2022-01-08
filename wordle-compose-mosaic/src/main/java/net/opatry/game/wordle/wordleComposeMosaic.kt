package net.opatry.game.wordle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.runMosaic
import kotlinx.coroutines.delay

suspend fun main() = runMosaic {
    var count by mutableStateOf(0)

    setContent {
        Text("The count is: $count")
    }

    for (i in 1..20) {
        delay(250)
        count = i
    }
}