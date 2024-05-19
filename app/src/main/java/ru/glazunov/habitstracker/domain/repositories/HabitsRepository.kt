package ru.glazunov.habitstracker.domain.repositories

import android.util.Log
import kotlinx.coroutines.flow.Flow
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitType
import ru.glazunov.habitstracker.domain.models.Ordering
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitsRepository @Inject constructor (
    private val localRepository: ILocalHabitsRepository,
    private val syncRepository: ISyncHabitsRepository
) {
    suspend fun put(habit: Habit){
        log("Put habit $habit")
        localRepository.put(habit)
        syncRepository.markModified(habit.id)
    }

    suspend fun delete(habit: Habit) = delete(habit.id)

    suspend fun delete(id: UUID){
        log("Deleting habit $id")
        localRepository.delete(id)
        syncRepository.markDeleted(id)
    }

    suspend fun get(id: UUID): Habit?{
        log("Getting habit with id $id")
        return localRepository.get(id)
    }

    suspend fun addDoneDate(date: Date, id: UUID){
        log("Add done date $date for habit $id")
        localRepository.addDoneDate(date, id)
        syncRepository.markDone(id)
    }

    fun get(
        habitType: HabitType,
        namePrefix: String = "",
        ordering: Ordering = Ordering.Ascending
    ): Flow<List<Habit>> = localRepository.getByTypeAndPrefix(habitType, "$namePrefix%", ordering)

    private fun log(message: String){
        Log.d("HabitsRepository", message)
    }
}