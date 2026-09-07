package com.multipoisson.app.data.db.dao

import androidx.room.*
import com.multipoisson.app.data.db.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles ORDER BY createdAt ASC")
    suspend fun getAll(): List<ProfileEntity>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getById(id: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: ProfileEntity)

    @Update
    suspend fun update(profile: ProfileEntity)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun count(): Int
}
