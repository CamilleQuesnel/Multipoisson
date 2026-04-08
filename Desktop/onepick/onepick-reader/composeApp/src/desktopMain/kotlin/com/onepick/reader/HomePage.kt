package com.onepick.reader

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun HomePage(
    chapters: List<Chapter>,
    progress: ReadingProgress,
    thumbnailCache: ImageCache,
    onChapterClick: (Int) -> Unit
) {
    val gridState = rememberLazyGridState()

    // Scroll automatique vers le dernier chapitre lu
    LaunchedEffect(Unit) {
        val lastIdx = (progress.lastChapter?.minus(1)) ?: return@LaunchedEffect
        if (lastIdx in chapters.indices) {
            gridState.scrollToItem(lastIdx.coerceAtLeast(0))
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF111111))) {

        // En-tête
        Surface(color = Color(0xFF0D0D0D), shadowElevation = 4.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "OnePick Reader",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEE0000)
                    )
                    Text(
                        "${chapters.size} chapitres · ${progress.readChapters.size} lu(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }
            }
        }

        // Grille + barre de scroll
        Box(modifier = Modifier.weight(1f)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4), // 4 colonnes desktop (2 pour Android)
                state = gridState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = chapters.size,
                    key = { idx -> chapters[idx].dir.absolutePath }
                ) { idx ->
                    ChapterCard(
                        chapter = chapters[idx],
                        chapterNumber = idx + 1,
                        isRead = progress.isRead(idx + 1),
                        isCurrent = progress.isCurrent(idx + 1),
                        thumbnailCache = thumbnailCache,
                        onClick = { onChapterClick(idx) }
                    )
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(gridState),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun ChapterCard(
    chapter: Chapter,
    chapterNumber: Int,
    isRead: Boolean,
    isCurrent: Boolean,
    thumbnailCache: ImageCache,
    onClick: () -> Unit
) {
    var thumbnail by remember(chapter) {
        mutableStateOf<ImageBitmap?>(
            chapter.pages.firstOrNull()?.let { thumbnailCache.get(it) }
        )
    }

    LaunchedEffect(chapter) {
        chapter.pages.firstOrNull()?.let { firstPage ->
            if (thumbnail == null) thumbnail = thumbnailCache.getOrLoad(firstPage)
        }
    }

    val thumbnailAlpha = if (isRead && !isCurrent) 0.5f else 1f
    val textColor = if (isRead && !isCurrent) Color(0xFF888888) else Color.White

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isCurrent)
                    Modifier.border(2.dp, Color(0xFFEE0000), RoundedCornerShape(8.dp))
                else
                    Modifier
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        // Miniature
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.65f)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        ) {
            if (thumbnail != null) {
                Image(
                    bitmap = thumbnail!!,
                    contentDescription = "Miniature ${chapter.name}",
                    modifier = Modifier.fillMaxSize().alpha(thumbnailAlpha),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2A2A2A)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFEE0000),
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            // Badge état lecture
            when {
                isCurrent -> Surface(
                    color = Color(0xFFEE0000),
                    shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        "EN COURS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                isRead -> Surface(
                    color = Color(0x99000000),
                    shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        "✓ Lu",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFAAAAAA),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Nom du chapitre
        Text(
            text = "Chapitre $chapterNumber",
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
