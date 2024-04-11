package ru.glazunov.habitstracker.repository

import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.models.HabitType
import java.util.*
import kotlin.collections.ArrayList

class MemoryRepository: IHabitsRepository {
    private val habits = HashMap<UUID, HabitInfo>()

    override fun putHabit(habitInfo: HabitInfo) {
        habits[habitInfo.id] = habitInfo
    }

    override fun getHabits(): ArrayList<HabitInfo> = ArrayList(habits.values)

    override fun getPositiveHabits(): ArrayList<HabitInfo> =
        ArrayList(habits.values.filter { habit ->  habit.type == HabitType.POSITIVE })

    override fun getNegativeHabits(): ArrayList<HabitInfo> =
        ArrayList(habits.values.filter { habit ->  habit.type == HabitType.NEGATIVE })
}