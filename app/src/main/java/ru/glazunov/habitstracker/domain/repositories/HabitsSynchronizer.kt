package ru.glazunov.habitstracker.domain.repositories

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class HabitsSynchronizer(
    private val syncRepository: ISyncHabitsRepository,
    private val localRepository: ILocalHabitsRepository,
    private val remoteRepository: IRemoteHabitsRepository
) {
    suspend fun observeModifications() = withContext(IO) {
        syncRepository.getModified().collect {
            for (entry in it) {
                val habit = localRepository.get(entry.id) ?: continue
                val networkId = remoteRepository.put(habit) ?: continue
                syncRepository.markSynchronised(entry.id, networkId)
            }
        }
    }

    suspend fun observeDeleted() = withContext(IO) {
        syncRepository.getDeleted().collect {
            for (entry in it)
                if (entry.remoteId != null && remoteRepository.delete(entry.remoteId))
                    syncRepository.delete(entry.id)
        }
    }
}