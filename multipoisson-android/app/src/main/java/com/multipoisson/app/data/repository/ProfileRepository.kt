package com.multipoisson.app.data.repository

import android.content.Context
import com.multipoisson.app.data.db.AppDatabase
import com.multipoisson.app.data.db.entity.ProfileEntity
import com.multipoisson.app.domain.FishId
import com.multipoisson.app.domain.SchoolYear
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ProfileRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).profileDao()

    data class Profile(
        val id: String,
        val name: String,
        val birthDateMs: Long,
        val schoolYear: SchoolYear,
        val activeMascotId: String,
        val createdAt: Long,
    )

    fun observeAll(): Flow<List<Profile>> =
        dao.observeAll().mapEntities()

    suspend fun getAll(): List<Profile> =
        dao.getAll().map { it.toDomain() }

    suspend fun getById(id: String): Profile? =
        dao.getById(id)?.toDomain()

    suspend fun count(): Int = dao.count()

    suspend fun create(
        name: String,
        birthDateMs: Long,
        schoolYear: SchoolYear,
    ): Profile {
        val entity = ProfileEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            birthDateMs = birthDateMs,
            schoolYear = schoolYear.name,
            activeMascotId = "poisson_table_00_gris_clair",
            createdAt = System.currentTimeMillis(),
        )
        dao.upsert(entity)
        return entity.toDomain()
    }

    suspend fun update(
        id: String,
        name: String,
        birthDateMs: Long,
        schoolYear: SchoolYear,
    ) {
        val existing = dao.getById(id) ?: return
        dao.update(existing.copy(name = name, birthDateMs = birthDateMs, schoolYear = schoolYear.name))
    }

    suspend fun setActiveMascot(id: String, mascotId: String) {
        val existing = dao.getById(id) ?: return
        dao.update(existing.copy(activeMascotId = mascotId))
    }

    suspend fun delete(id: String) = dao.deleteById(id)

    // ── Mappers ────────────────────────────────────────────────────────────

    private fun ProfileEntity.toDomain() = Profile(
        id = id,
        name = name,
        birthDateMs = birthDateMs,
        schoolYear = SchoolYear.fromLabel(schoolYear),
        activeMascotId = activeMascotId,
        createdAt = createdAt,
    )

    private fun Flow<List<ProfileEntity>>.mapEntities(): Flow<List<Profile>> =
        map { list -> list.map { it.toDomain() } }
}
