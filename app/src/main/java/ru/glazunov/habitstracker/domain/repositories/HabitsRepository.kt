package ru.glazunov.habitstracker.domain.repositories

import android.util.Log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitType
import ru.glazunov.habitstracker.domain.models.Ordering
import java.util.UUID

class HabitsRepository(
    private val localRepository: ILocalHabitsRepository,
    private val remoteRepository: IRemoteHabitsRepository,
    private val syncRepository: ISyncHabitsRepository
) {
    suspend fun put(habit: Habit){
        log("Put habit $habit")
        localRepository.put(habit)
        syncRepository.markModified(habit.id)
    }

    suspend fun reload(){
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

    suspend fun delete(habit: Habit){
        log("Deleting habit $habit")
        localRepository.delete(habit.id)
        syncRepository.markDeleted(habit.id)
    }

    suspend fun get(id: UUID): Habit?{
        log("Getting habit with id $id")
        return localRepository.get(id)
    }

    fun get(
        habitType: HabitType,
        namePrefix: String = "",
        ordering: Ordering = Ordering.Ascending
    ): Flow<List<Habit>> = localRepository.getByTypeAndPrefix(habitType, namePrefix, ordering)

    private fun log(message: String){
        Log.d("HabitsRepository", message)
    }
}