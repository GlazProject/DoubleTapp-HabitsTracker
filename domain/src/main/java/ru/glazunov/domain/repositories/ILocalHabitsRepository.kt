package ru.glazunov.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.glazunov.domain.models.Habit
import ru.glazunov.domain.models.HabitType
import ru.glazunov.domain.models.Ordering
import java.util.Date
import java.util.UUID

interface ILocalHabitsRepository {
    suspend fun delete(id: UUID)

    suspend fun put(habit: Habit)

    suspend fun get(id: UUID) : Habit?

    suspend fun addDoneDate(date: Date, id: UUID)

    fun getByTypeAndPrefix(habitType: HabitType,
                           namePrefix: String = "",
                           ordering: Ordering = Ordering.Ascending): Flow<List<Habit>>
}