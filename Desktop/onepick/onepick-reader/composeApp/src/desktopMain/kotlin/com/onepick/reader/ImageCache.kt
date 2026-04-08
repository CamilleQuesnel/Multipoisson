package com.onepick.reader

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.*
import org.jetbrains.skia.Image as SkiaImage
import java.io.File

class ImageCache(private val maxSize: Int = 80) {

    private val lock = Any()
    private val cache = object : LinkedHashMap<String, ImageBitmap>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<String, ImageBitmap>) = size > maxSize
    }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val inFlight = mutableSetOf<String>()

    fun get(file: File): ImageBitmap? = synchronized(lock) { cache[file.absolutePath] }

    /** Charge depuis disque si absent du cache. Suspend jusqu'à ce que l'image soit prête. */
    suspend fun getOrLoad(file: File): ImageBitmap? {
        get(file)?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                // Double-check après switch de thread
                get(file) ?: run {
                    val bitmap = decode(file)
                    synchronized(lock) { cache[file.absolutePath] = bitmap }
                    bitmap
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    /** Lance le chargement en arrière-plan pour les fichiers pas encore en cache. */
    fun preload(files: List<File>) {
        files.forEach { file ->
            val key = file.absolutePath
            val alreadyCached = synchronized(lock) { cache.containsKey(key) }
            if (!alreadyCached) {
                val launching = synchronized(lock) {
                    if (key in inFlight) false
                    else { inFlight.add(key); true }
                }
                if (launching) {
                    scope.launch {
                        try {
                            val bitmap = decode(file)
                            synchronized(lock) { cache[key] = bitmap }
                        } catch (_: Exception) {
                        } finally {
                            synchronized(lock) { inFlight.remove(key) }
                        }
                    }
                }
            }
        }
    }

    private fun decode(file: File): ImageBitmap =
        SkiaImage.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
}
