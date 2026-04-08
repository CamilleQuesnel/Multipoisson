package com.onepick.reader

import java.io.File

data class ReadingProgress(
    val lastChapter: Int? = null,        // numéro 1-indexé
    val readChapters: Set<Int> = emptySet()  // numéros 1-indexés
) {
    /** Marque le chapitre comme lu et le définit comme dernier chapitre. */
    fun withChapterRead(chapterNumber: Int) = copy(
        lastChapter = chapterNumber,
        readChapters = readChapters + chapterNumber
    )

    fun isRead(chapterNumber: Int) = chapterNumber in readChapters
    fun isCurrent(chapterNumber: Int) = chapterNumber == lastChapter
}

class ProgressManager(val file: File) {

    fun load(): ReadingProgress {
        if (!file.exists()) return ReadingProgress()
        return try {
            val json = file.readText()
            val lastChapter = Regex(""""lastChapter"\s*:\s*(\d+)""")
                .find(json)?.groupValues?.get(1)?.toIntOrNull()
            val readChapters = Regex(""""readChapters"\s*:\s*\[([^\]]*)]""")
                .find(json)?.groupValues?.get(1)
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?.toSet() ?: emptySet()
            ReadingProgress(lastChapter, readChapters)
        } catch (_: Exception) {
            ReadingProgress()
        }
    }

    fun save(progress: ReadingProgress) {
        try {
            val list = progress.readChapters.sorted().joinToString(", ")
            val last = progress.lastChapter?.toString() ?: "null"
            file.writeText("{\n  \"lastChapter\": $last,\n  \"readChapters\": [$list]\n}\n")
        } catch (_: Exception) { }
    }
}
