package com.onepick.reader

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(900.dp, 900.dp))

    // Cherche one_piece/ dans le dossier parent (onepick/) quand lancé depuis onepick-reader/
    val mangaDir = File("C:\\Users\\cques\\Desktop\\onepick\\one_piece")
        .let { workDir ->
            workDir.resolve("../one_piece").canonicalFile.takeIf { it.exists() }
                ?: workDir.resolve("one_piece").takeIf { it.exists() }
                ?: workDir.resolve("../one_piece").canonicalFile
        }

    Window(
        onCloseRequest = ::exitApplication,
        title = "OnePick Reader",
        state = windowState
    ) {
        App(mangaDir = mangaDir)
    }
}
