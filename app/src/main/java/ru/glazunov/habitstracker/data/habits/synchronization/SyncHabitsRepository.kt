package ru.glazunov.habitstracker.data.habits.synchronization

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.glazunov.habitstracker.data.habits.synchronization.models.HabitState
import ru.glazunov.habitstracker.data.habits.synchronization.models.HabitStatus
import ru.glazunov.habitstracker.data.habits.synchronization.providers.HabitSyncDao
import ru.glazunov.habitstracker.domain.repositories.ISyncHabitsRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncHabitsRepository @Inject constructor (private val dao: HabitSyncDao)
    : ISyncHabitsRepository {
    override suspend fun markSynchronised(id: UUID, remoteId: String?) {
        val record = dao.getRecord(id.toString())
        dao.put(
            HabitState(
                habitId = id,
                networkId = remoteId ?: record?.networkId,
                status = HabitStatus.Synchronized
            )
        )
    }

    override suspend fun markModified(id: UUID, remoteId: String?) {
        val record = dao.getRecord(id.toString())
        dao.put(
            HabitState(
                habitId = id,
                networkId = remoteId ?: record?.networkId,
                status = HabitStatus.Modified
            )
        )
    }

    override suspend fun markDeleted(id: UUID) {
        val record = dao.getRecord(id.toString())
        dao.put(
            HabitState(
                habitId = id,
                networkId = record?.networkId,
                status = HabitStatus.Deleted
            )
        )
    }

    override suspend fun delete(id: UUID) = dao.deleteRecord(id.toString())

    override suspend fun getNotDeleted(): List<ISyncHabitsRepository.SyncRecord> =
        dao.getNotDeleted().map {ISyncHabitsRepository.SyncRecord(it.habitId, it.networkId)}

    override fun getDeleted(): Flow<List<ISyncHabitsRepository.SyncRecord>> =
        dao.getDeleted().map {it.map{ record -> ISyncHabitsRepository.SyncRecord(record.habitId, record.networkId)}}

    override fun getModified(): Flow<List<ISyncHabitsRepository.SyncRecord>> =
        dao.getModified().map {it.map{ record -> ISyncHabitsRepository.SyncRecord(record.habitId, record.networkId)}}

    override suspend fun gerRecord(id: UUID): ISyncHabitsRepository.SyncRecord? {
        val record = dao.getRecord(id.toString()) ?: return null
        return ISyncHabitsRepository.SyncRecord(record.habitId, record.networkId)
    }
}