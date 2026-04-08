package com.onepick.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.io.File

private enum class Screen { HOME, READER }

@Composable
fun App(mangaDir: File) {
    val chapterManager = remember(mangaDir) { ChapterManager(mangaDir) }
    val imageCache = remember { ImageCache(maxSize = 80) }
    val thumbnailCache = remember { ImageCache(maxSize = 25) }
    val progressManager = remember(mangaDir) {
        ProgressManager(
            mangaDir.parentFile?.resolve("reading_progress.json") ?: File("reading_progress.json")
        )
    }

    var screen by remember { mutableStateOf(Screen.HOME) }
    var currentChapterIndex by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(progressManager.load()) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        val chapters = chapterManager.chapters

        if (chapters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF111111)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aucun chapitre trouvé dans :\n${mangaDir.absolutePath}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            when (screen) {
                Screen.HOME -> HomePage(
                    chapters = chapters,
                    progress = progress,
                    thumbnailCache = thumbnailCache,
                    onChapterClick = { idx ->
                        currentChapterIndex = idx
                        screen = Screen.READER
                    }
                )

                Screen.READER -> WebtoonReader(
                    chapter = chapters[currentChapterIndex],
                    totalChapters = chapters.size,
                    imageCache = imageCache,
                    onHome = {
                        // Marque le chapitre en cours comme lu et retourne à l'accueil
                        progress = progress.withChapterRead(currentChapterIndex + 1)
                        progressManager.save(progress)
                        screen = Screen.HOME
                    },
                    onPrevChapter = {
                        if (currentChapterIndex > 0) currentChapterIndex--
                    },
                    onNextChapter = {
                        if (currentChapterIndex < chapters.size - 1) currentChapterIndex++
                    },
                    onChapterSelect = { idx -> currentChapterIndex = idx }
                )
            }
        }
    }
}
