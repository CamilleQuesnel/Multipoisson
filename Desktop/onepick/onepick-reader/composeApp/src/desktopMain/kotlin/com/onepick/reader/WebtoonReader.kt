package com.onepick.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import java.io.File
import kotlin.math.roundToInt

private val BG = Color(0xFF111111)
private val BAR_BG = Color(0xFF0D0D0D)
private val BTN_COLOR = Color(0xFF2C2C2C)

private const val ZOOM_MIN = 0.5f
private const val ZOOM_MAX = 3.0f
private const val ZOOM_STEP = 0.1f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WebtoonReader(
    chapter: Chapter,
    totalChapters: Int,
    imageCache: ImageCache,
    onHome: () -> Unit,
    onPrevChapter: () -> Unit,
    onNextChapter: () -> Unit,
    onChapterSelect: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    var zoom by remember { mutableStateOf(1f) }
    var zoomLabelVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val density = LocalDensity.current

    // Arrondi au palier de 10% le plus proche, applique les bornes
    fun applyZoom(raw: Float) {
        zoom = ((raw * 10f).roundToInt() / 10f).coerceIn(ZOOM_MIN, ZOOM_MAX)
    }

    // Indicateur : s'affiche au changement de zoom, disparaît après 2 s d'inactivité
    LaunchedEffect(zoom) {
        zoomLabelVisible = true
        kotlinx.coroutines.delay(2_000)
        zoomLabelVisible = false
    }

    // Reset au changement de chapitre
    LaunchedEffect(chapter.index) {
        zoom = 1f
        zoomLabelVisible = false
        listState.scrollToItem(0)
    }

    // Préchargement initial
    LaunchedEffect(chapter) {
        imageCache.preload(chapter.pages.take(10))
    }

    // Préchargement adaptatif
    val firstVisible by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    LaunchedEffect(firstVisible) {
        val pages = chapter.pages
        val start = (firstVisible - 2).coerceAtLeast(0)
        val end = (firstVisible + 12).coerceAtMost(pages.size)
        imageCache.preload(pages.subList(start, end))
    }

    // Pinch-to-zoom (trackpad desktop / Android futur)
    val transformableState = rememberTransformableState { zoomChange, _, _ ->
        zoom = (zoom * zoomChange).coerceIn(ZOOM_MIN, ZOOM_MAX)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BG)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when {
                        event.isCtrlPressed -> when (event.key) {
                            Key.Equals, Key.NumPadAdd     -> { applyZoom(zoom + ZOOM_STEP); true }
                            Key.Minus, Key.NumPadSubtract -> { applyZoom(zoom - ZOOM_STEP); true }
                            Key.Zero,  Key.NumPad0        -> { applyZoom(1f);               true }
                            else -> false
                        }
                        // Navigation entre chapitres par flèches clavier
                        event.key == Key.DirectionRight || event.key == Key.DirectionDown -> {
                            onNextChapter(); true
                        }
                        event.key == Key.DirectionLeft || event.key == Key.DirectionUp -> {
                            onPrevChapter(); true
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        TopBar(
            chapterName = chapter.name,
            chapterIndex = chapter.index,
            totalChapters = totalChapters,
            onHome = onHome,
            onPrevChapter = onPrevChapter,
            onNextChapter = onNextChapter,
            onChapterSelect = onChapterSelect
        )

        // Zone de lecture
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {

            val horizScroll = rememberScrollState()

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val containerWidth = maxWidth

                // Recentre le scroll horizontal à chaque changement de zoom
                LaunchedEffect(zoom) {
                    with(density) {
                        val overflow = ((containerWidth * zoom - containerWidth).toPx())
                            .toInt().coerceAtLeast(0)
                        horizScroll.animateScrollTo(overflow / 2)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        // Pinch / trackpad
                        .transformable(state = transformableState, canPan = { false })
                        // Ctrl + molette → zoom (intercepte avant LazyColumn)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    if (event.type == PointerEventType.Scroll &&
                                        event.keyboardModifiers.isCtrlPressed
                                    ) {
                                        event.changes.forEach { it.consume() }
                                        val dy = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                                        when {
                                            dy < 0 -> applyZoom(zoom + ZOOM_STEP)
                                            dy > 0 -> applyZoom(zoom - ZOOM_STEP)
                                        }
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.TopCenter
                ) {
                    // Scroll horizontal activé quand zoom > 1
                    Box(modifier = Modifier.horizontalScroll(horizScroll)) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.width(containerWidth * zoom),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            itemsIndexed(
                                items = chapter.pages,
                                key = { _, file -> file.absolutePath }
                            ) { index, pageFile ->
                                PageImage(
                                    file = pageFile,
                                    imageCache = imageCache,
                                    pageNumber = index + 1
                                )
                            }
                            item {
                                BottomChapterNav(
                                    chapterIndex = chapter.index,
                                    totalChapters = totalChapters,
                                    onPrevChapter = onPrevChapter,
                                    onNextChapter = onNextChapter
                                )
                            }
                        }
                    }
                }
            }

            // Indicateur de zoom (overlay haut-droite)
            ZoomIndicator(zoom = zoom, visible = zoomLabelVisible)
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

// ─────────────────────────────────────────────────────────────────────────────
// Indicateur de zoom
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ZoomIndicator(zoom: Float, visible: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                color = Color(0xCC000000),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.padding(top = 10.dp, end = 14.dp)
            ) {
                Text(
                    text = "${(zoom * 100).roundToInt()}%",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Page individuelle
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PageImage(file: File, imageCache: ImageCache, pageNumber: Int) {
    var bitmap by remember(file) { mutableStateOf<ImageBitmap?>(imageCache.get(file)) }

    LaunchedEffect(file) {
        if (bitmap == null) bitmap = imageCache.getOrLoad(file)
    }

    Box(
        modifier = Modifier.fillMaxWidth().padding(bottom = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!,
                contentDescription = "Page $pageNumber",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(Color(0xFF1E1E1E)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFFEE0000),
                    modifier = Modifier.size(36.dp),
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Barre de navigation haute
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TopBar(
    chapterName: String,
    chapterIndex: Int,
    totalChapters: Int,
    onHome: () -> Unit,
    onPrevChapter: () -> Unit,
    onNextChapter: () -> Unit,
    onChapterSelect: (Int) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(color = BAR_BG, shadowElevation = 6.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Groupe gauche : Accueil + Précédent
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onHome) {
                    Text(text = "⌂", color = Color(0xFF888888),
                        style = MaterialTheme.typography.titleMedium)
                }
                IconButton(onClick = onPrevChapter, enabled = chapterIndex > 0) {
                    Text(
                        text = "◀",
                        color = if (chapterIndex > 0) Color.White else Color(0xFF555555),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Box {
                TextButton(onClick = { menuExpanded = true }) {
                    Text(text = chapterName, color = Color.White,
                        style = MaterialTheme.typography.titleMedium)
                    Text(text = "  ▼", color = Color(0xFF888888),
                        style = MaterialTheme.typography.labelSmall)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    repeat(totalChapters) { idx ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Chapitre ${idx + 1}",
                                    color = if (idx == chapterIndex) Color(0xFFEE0000)
                                            else Color.Unspecified
                                )
                            },
                            onClick = { onChapterSelect(idx); menuExpanded = false }
                        )
                    }
                }
            }

            IconButton(onClick = onNextChapter, enabled = chapterIndex < totalChapters - 1) {
                Text(
                    text = "▶",
                    color = if (chapterIndex < totalChapters - 1) Color.White else Color(0xFF555555),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Navigation bas de chapitre
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BottomChapterNav(
    chapterIndex: Int,
    totalChapters: Int,
    onPrevChapter: () -> Unit,
    onNextChapter: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (chapterIndex > 0) {
            Button(
                onClick = onPrevChapter,
                colors = ButtonDefaults.buttonColors(containerColor = BTN_COLOR)
            ) { Text("◀  Chapitre précédent", color = Color.White) }
        }
        if (chapterIndex < totalChapters - 1) {
            Button(
                onClick = onNextChapter,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEE0000))
            ) { Text("Chapitre suivant  ▶", color = Color.White) }
        }
    }
}
