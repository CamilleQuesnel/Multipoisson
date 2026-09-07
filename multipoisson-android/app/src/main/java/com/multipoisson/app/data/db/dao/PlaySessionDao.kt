package com.multipoisson.app.data.db.dao

import androidx.room.*
import com.multipoisson.app.data.db.entity.PlaySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaySessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: PlaySessionEntity)

    @Query("SELECT * FROM play_sessions WHERE profileId = :profileId ORDER BY timestamp DESC")
    fun observeByProfile(profileId: String): Flow<List<PlaySessionEntity>>

    @Query("SELECT * FROM play_sessions WHERE profileId = :profileId ORDER BY timestamp DESC")
    suspend fun getByProfile(profileId: String): List<PlaySessionEntity>

    @Query("SELECT COUNT(DISTINCT dateStr) FROM play_sessions WHERE profileId = :profileId")
    suspend fun countDistinctDays(profileId: String): Int

    @Query("""
        SELECT * FROM play_sessions
        WHERE profileId = :profileId AND timerEnabled = 1
        ORDER BY timestamp DESC
    """)
    suspend fun getTimedByProfile(profileId: String): List<PlaySessionEntity>

    @Query("""
        SELECT * FROM play_sessions
        WHERE profileId = :profileId
        ORDER BY timestamp DESC LIMIT :limit
    """)
    suspend fun getRecentByProfile(profileId: String, limit: Int): List<PlaySessionEntity>
}
