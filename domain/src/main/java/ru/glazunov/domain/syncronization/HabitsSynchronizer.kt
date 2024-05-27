package ru.glazunov.domain.syncronization

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.glazunov.domain.repositories.ILocalHabitsRepository
import ru.glazunov.domain.repositories.IRemoteHabitsRepository
import ru.glazunov.domain.repositories.ISyncHabitsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitsSynchronizer @Inject constructor (
    private val syncRepository: ISyncHabitsRepository,
    private val localRepository: ILocalHabitsRepository,
    private val remoteRepository: IRemoteHabitsRepository
) {
    suspend fun observeModifications() = withContext(IO) {
        syncRepository.getModified().collect {
            records@
            for (entry in it) {
                val habit = localRepository.get(entry.id) ?: continue
                habit.remoteId = entry.remoteId
                val networkId = remoteRepository.put(habit) ?: continue

                for (date in habit.doneDates.takeLast(entry.doneTimes))
                    if (!remoteRepository.done(date, networkId))
                        continue@records

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

    suspend fun refresh(){
        val syncData = syncRepository.getNotDeleted()
        val remoteHabits = remoteRepository.getAll()

        for (habit in remoteHabits) {
            val entry = syncData.firstOrNull{it.remoteId == habit.remoteId}
            if (entry != null) habit.id = entry.id
            localRepository.put(habit)
            syncRepository.markSynchronised(habit.id, habit.remoteId)
        }

        syncData.filter { it.remoteId != null }
            .filter { habit -> remoteHabits.firstOrNull{it.remoteId == habit.remoteId} == null }
            .forEach{ localRepository.delete(it.id) }
    }
}