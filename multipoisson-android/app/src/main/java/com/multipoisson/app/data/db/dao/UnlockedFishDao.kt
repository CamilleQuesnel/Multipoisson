package com.multipoisson.app.data.db.dao

import androidx.room.*
import com.multipoisson.app.data.db.entity.UnlockedFishEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnlockedFishDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(fish: UnlockedFishEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(fish: List<UnlockedFishEntity>)

    @Query("SELECT * FROM unlocked_fish WHERE profileId = :profileId ORDER BY unlockedAt ASC")
    fun observeByProfile(profileId: String): Flow<List<UnlockedFishEntity>>

    @Query("SELECT * FROM unlocked_fish WHERE profileId = :profileId ORDER BY unlockedAt ASC")
    suspend fun getByProfile(profileId: String): List<UnlockedFishEntity>

    @Query("SELECT fishId FROM unlocked_fish WHERE profileId = :profileId")
    suspend fun getUnlockedIds(profileId: String): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_fish WHERE profileId = :profileId AND fishId = :fishId)")
    suspend fun isUnlocked(profileId: String, fishId: String): Boolean
}
