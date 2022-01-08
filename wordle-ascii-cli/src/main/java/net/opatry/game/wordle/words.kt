package net.opatry.game.wordle

// source: https://github.com/octokatherine/word-master/blob/58a4534e58a058e3fbccfcc99c881992d319b159/src/data/words.js
val words = Wordle::class.java.getResource("/words.txt")?.readText()?.split(Regex("\\s")) ?: emptyList()
