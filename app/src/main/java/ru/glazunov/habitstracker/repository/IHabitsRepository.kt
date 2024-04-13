package ru.glazunov.habitstracker.repository

import ru.glazunov.habitstracker.models.HabitInfo
import java.util.*
import kotlin.collections.ArrayList

interface IHabitsRepository {
    fun putHabit(habitInfo: HabitInfo)
    fun getHabits() : ArrayList<HabitInfo>
    fun getHabit(id: UUID) : HabitInfo
    fun getPositiveHabits() : ArrayList<HabitInfo>
    fun getNegativeHabits() : ArrayList<HabitInfo>
}