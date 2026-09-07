package com.multipoisson.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "play_sessions",
    indices = [Index("profileId"), Index("dateStr")],
)
data class PlaySessionEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    /** "2026-08-30" — used to count distinct play days for regularity rewards */
    val dateStr: String,
    /** "TABLE", "MIXTE", or "DEFI" */
    val mode: String,
    /** JSON array of table indices, e.g. "[7]" or "[3,5,7]" */
    val tables: String,
    val questionCount: Int,
    val correctCount: Int,
    val durationSeconds: Int,
    val timerEnabled: Boolean,
    val timestamp: Long,
)
