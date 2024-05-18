package ru.glazunov.habitstracker.domain.repositories

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ISyncHabitsRepository {
    suspend fun markSynchronised(id: UUID, remoteId: String? = null)
    suspend fun markModified(id: UUID, remoteId: String? = null)
    suspend fun markDeleted(id: UUID)
    suspend fun delete(id: UUID)

    fun getDeleted(): Flow<List<SyncRecord>>
    fun getModified(): Flow<List<SyncRecord>>
    suspend fun getNotDeleted(): List<SyncRecord>
    suspend fun gerRecord(id: UUID): SyncRecord?

    data class SyncRecord(val id: UUID, val remoteId: String?)
}