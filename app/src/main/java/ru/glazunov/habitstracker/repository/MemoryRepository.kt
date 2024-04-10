package ru.glazunov.habitstracker.repository

import android.content.Context
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.R
import java.util.*
import kotlin.collections.ArrayList

class MemoryRepository(context: Context): IHabitsRepository {
    private val positive = context.getString(R.string.positive_habit)
    private val negative = context.getString(R.string.negative_habit)
    private val habits = HashMap<UUID, HabitInfo>()

    override fun putHabit(habitInfo: HabitInfo) {
        habits[habitInfo.id] = habitInfo
    }

    override fun getHabits(): ArrayList<HabitInfo> = ArrayList(habits.values)

    override fun getPositiveHabits(): ArrayList<HabitInfo> =
        ArrayList(habits.values.filter { habit ->  habit.type == positive })

    override fun getNegativeHabits(): ArrayList<HabitInfo> =
        ArrayList(habits.values.filter { habit ->  habit.type == negative })
}