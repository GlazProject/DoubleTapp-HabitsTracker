package ru.glazunov.habitstracker.repository

import androidx.lifecycle.LiveData
import ru.glazunov.habitstracker.models.HabitInfo
import java.util.*

interface IHabitsRepository {
    fun putHabit(habitInfo: HabitInfo)
    fun getHabits() : LiveData<List<HabitInfo>>
    fun getHabit(id: UUID) : HabitInfo
    fun getPositiveHabits() : LiveData<List<HabitInfo>>
    fun getNegativeHabits() : LiveData<List<HabitInfo>>
}