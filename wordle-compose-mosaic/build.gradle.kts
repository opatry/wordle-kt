plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.mosaic)
    application
}

dependencies {
    implementation(libs.jline) {
        because("need to handle terminal keyboard input")
    }
    implementation(libs.turtle) {
        because("need to copy results to clipboard (using `pbcopy`, `xclip`, `clip` or equivalent)")
    }
    implementation(project(":word-data"))
    implementation(project(":game-logic"))
}

application {
    mainClass = "net.opatry.game.wordle.mosaic.WordleComposeMosaicKt"
}