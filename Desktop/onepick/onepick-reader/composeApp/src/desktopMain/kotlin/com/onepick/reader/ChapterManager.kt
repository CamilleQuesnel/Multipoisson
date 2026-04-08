package com.onepick.reader

import java.io.File

data class Chapter(
    val index: Int,
    val name: String,
    val dir: File,
    val pages: List<File>
)

class ChapterManager(val mangaDir: File) {

    val chapters: List<Chapter> by lazy {
        val dirs = mangaDir.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name }
            ?: return@lazy emptyList()

        dirs.mapIndexed { idx, dir ->
            val pages = dir.listFiles()
                ?.filter { it.extension.lowercase() in listOf("jpg", "jpeg", "png", "webp") }
                ?.sortedBy { it.name }
                ?: emptyList()
            Chapter(
                index = idx,
                name = dir.name.replace("_", " ").replaceFirstChar { it.uppercase() },
                dir = dir,
                pages = pages
            )
        }.filter { it.pages.isNotEmpty() }
    }
}
