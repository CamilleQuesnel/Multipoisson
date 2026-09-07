package com.multipoisson.app.data.db.dao

import androidx.room.*
import com.multipoisson.app.data.db.entity.QuestionAttemptEntity

@Dao
interface QuestionAttemptDao {

    @Insert
    suspend fun insert(attempt: QuestionAttemptEntity)

    @Insert
    suspend fun insertAll(attempts: List<QuestionAttemptEntity>)

    @Query("SELECT * FROM question_attempts WHERE profileId = :profileId AND isCorrect = 1")
    suspend fun getCorrectByProfile(profileId: String): List<QuestionAttemptEntity>

    @Query("""
        SELECT * FROM question_attempts
        WHERE profileId = :profileId AND tableN = :tableN AND isCorrect = 1
    """)
    suspend fun getCorrectByProfileAndTable(
        profileId: String,
        tableN: Int,
    ): List<QuestionAttemptEntity>

    @Query("SELECT COUNT(*) FROM question_attempts WHERE profileId = :profileId")
    suspend fun countByProfile(profileId: String): Int
}
