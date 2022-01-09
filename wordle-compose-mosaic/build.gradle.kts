plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.mosaic)
    application
}

dependencies {
    implementation(libs.jline) {
        because("need to handle terminal keyboard input")
    }
    implementation(project(":word-data"))
    implementation(project(":game-logic"))
}

application {
    mainClass = "net.opatry.game.wordle.mosaic.WordleComposeMosaicKt"
}