package ru.glazunov.habitstracker.data.habits

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import ru.glazunov.habitstracker.models.LocalHabit
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering
import java.util.UUID

interface IHabitsRepository {
    suspend fun putHabit(habit: LocalHabit)
    suspend fun syncHabits()
    suspend fun deleteHabit(habit: LocalHabit)
    fun getHabit(id: UUID): LiveData<LocalHabit>
    fun getHabits(
        habitType: HabitType,
        namePrefix: String = "",
        ordering: Ordering = Ordering.Ascending
    ): LiveData<List<LocalHabit>>
}