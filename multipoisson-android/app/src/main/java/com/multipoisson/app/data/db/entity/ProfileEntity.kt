package com.multipoisson.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val birthDateMs: Long,
    val schoolYear: String,
    val activeMascotId: String,
    val createdAt: Long,
)
