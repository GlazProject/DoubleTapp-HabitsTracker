package ru.glazunov.data.habits.synchronization

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.glazunov.data.habits.synchronization.models.HabitState
import ru.glazunov.data.habits.synchronization.models.HabitStatus
import ru.glazunov.data.habits.synchronization.providers.HabitSyncDao
import ru.glazunov.domain.repositories.ISyncHabitsRepository
import java.util.UUID

class SyncHabitsRepository(private val dao: HabitSyncDao)
    : ISyncHabitsRepository {
    override suspend fun markSynchronised(id: UUID, remoteId: String?) {
        val record = dao.getRecord(id.toString())
        dao.put(
            HabitState(
                habitId = id,
                networkId = remoteId ?: record?.networkId,
                status = HabitStatus.Synchronized,
                doneTimes = 0
            )
        )
    }

    override suspend fun markDone(id: UUID, remoteId: String?) {
        val record = dao.getRecord(id.toString())
        dao.put(
            HabitState(
                habitId = id,
                networkId = remoteId ?: record?.networkId,
                status = HabitStatus.Modified,
                doneTimes = (record?.doneTimes ?: 0) + 1
            )
        )
    }

    override suspend fun markModified(id: UUID, remoteId: String?) {
        val record = dao.getRecord(id.toString())
        dao.put(
            HabitState(
                habitId = id,
                networkId = remoteId ?: record?.networkId,
                status = HabitStatus.Modified,
                doneTimes = record?.doneTimes?: 0
            )
        )
    }

    override suspend fun markDeleted(id: UUID) {
        val record = dao.getRecord(id.toString())
        dao.put(
            HabitState(
                habitId = id,
                networkId = record?.networkId,
                status = HabitStatus.Deleted,
                0
            )
        )
    }

    override suspend fun delete(id: UUID) = dao.deleteRecord(id.toString())

    override suspend fun getNotDeleted(): List<ISyncHabitsRepository.SyncRecord> =
        dao.getNotDeleted().map {ISyncHabitsRepository.SyncRecord(it.habitId, it.networkId, it.doneTimes)}

    override fun getDeleted(): Flow<List<ISyncHabitsRepository.SyncRecord>> =
        dao.getDeleted().map {it.map{ record -> ISyncHabitsRepository.SyncRecord(record.habitId, record.networkId, record.doneTimes)}}

    override fun getModified(): Flow<List<ISyncHabitsRepository.SyncRecord>> =
        dao.getModified().map {it.map{ record -> ISyncHabitsRepository.SyncRecord(record.habitId, record.networkId, record.doneTimes)}}

    override suspend fun gerRecord(id: UUID): ISyncHabitsRepository.SyncRecord? {
        val record = dao.getRecord(id.toString()) ?: return null
        return ISyncHabitsRepository.SyncRecord(record.habitId, record.networkId, record.doneTimes)
    }
}