package com.multipoisson.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "question_attempts",
    indices = [Index("profileId"), Index("timestamp")],
)
data class QuestionAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: String,
    val sessionId: String,
    val tableN: Int,
    val operandA: Int,
    val operandB: Int,
    val isCorrect: Boolean,
    val responseTimeMs: Long,
    val timerEnabled: Boolean,
    val timestamp: Long,
)
