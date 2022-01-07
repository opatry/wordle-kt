package net.opatry.game.wordle

import java.text.Normalizer


fun String.toWordle(): String {
    // see https://stackoverflow.com/a/63523402/2551689
    return Normalizer
            .normalize(this, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .trim()
            .uppercase()
}