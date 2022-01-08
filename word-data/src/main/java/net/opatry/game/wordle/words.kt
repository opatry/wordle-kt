package net.opatry.game.wordle

object Words

// source: https://github.com/octokatherine/word-master/blob/58a4534e58a058e3fbccfcc99c881992d319b159/src/data/words.js
val words = Words::class.java.getResource("/words.txt")?.readText()?.split(Regex("\\s")) ?: emptyList()
