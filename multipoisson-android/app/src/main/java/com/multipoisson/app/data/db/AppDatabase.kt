package com.multipoisson.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.multipoisson.app.data.db.dao.*
import com.multipoisson.app.data.db.entity.*

@Database(
    entities = [
        ProfileEntity::class,
        QuestionAttemptEntity::class,
        PlaySessionEntity::class,
        UnlockedFishEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun questionAttemptDao(): QuestionAttemptDao
    abstract fun playSessionDao(): PlaySessionDao
    abstract fun unlockedFishDao(): UnlockedFishDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "multipoisson.db",
                ).build().also { INSTANCE = it }
            }
    }
}
