package ru.glazunov.habitstracker.data

import androidx.lifecycle.LiveData
import ru.glazunov.habitstracker.models.Habit
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering
import java.util.UUID

interface IHabitsRepository {
    suspend fun putHabit(habit: Habit)
    suspend fun syncHabits()
    fun getHabit(id: UUID): LiveData<Habit>
    fun getHabits(
        habitType: HabitType,
        namePrefix: String = "",
        ordering: Ordering = Ordering.Ascending
    ): LiveData<List<Habit>>
}