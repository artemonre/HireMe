package com.artemonre.hireme

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    System.setProperty("skiko.renderApi", "OPENGL")

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "HireMe",
        ) {
            App()
        }
    }
}