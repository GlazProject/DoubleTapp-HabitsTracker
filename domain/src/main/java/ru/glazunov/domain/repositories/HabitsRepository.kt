package ru.glazunov.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.glazunov.domain.di.DomainScope
import ru.glazunov.domain.models.Habit
import ru.glazunov.domain.models.HabitType
import ru.glazunov.domain.models.Ordering
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@DomainScope
class HabitsRepository @Inject constructor (
    private val localRepository: ILocalHabitsRepository,
    private val syncRepository: ISyncHabitsRepository
) {
    suspend fun put(habit: Habit){
        localRepository.put(habit)
        syncRepository.markModified(habit.id)
    }

    suspend fun delete(habit: Habit) = delete(habit.id)

    suspend fun delete(id: UUID){
        localRepository.delete(id)
        syncRepository.markDeleted(id)
    }

    suspend fun get(id: UUID): Habit? = localRepository.get(id)

    suspend fun addDoneDate(date: Date, id: UUID){
        localRepository.addDoneDate(date, id)
        syncRepository.markDone(id)
    }

    fun get(
        habitType: HabitType,
        namePrefix: String = "",
        ordering: Ordering = Ordering.Ascending
    ): Flow<List<Habit>> = localRepository.getByTypeAndPrefix(habitType, "$namePrefix%", ordering)
}