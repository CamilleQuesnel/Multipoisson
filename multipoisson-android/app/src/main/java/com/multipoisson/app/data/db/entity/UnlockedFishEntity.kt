package com.multipoisson.app.data.db.entity

import androidx.room.Entity

@Entity(tableName = "unlocked_fish", primaryKeys = ["profileId", "fishId"])
data class UnlockedFishEntity(
    val profileId: String,
    val fishId: String,
    val unlockedAt: Long,
)
