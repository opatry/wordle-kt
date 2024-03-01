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
    implementation(projects.wordData)
    implementation(projects.gameLogic)
}

application {
    mainClass = "net.opatry.game.wordle.mosaic.WordleComposeMosaicKt"
}