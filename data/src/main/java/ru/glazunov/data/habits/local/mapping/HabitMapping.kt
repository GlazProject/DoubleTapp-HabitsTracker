package ru.glazunov.data.habits.local.mapping

import ru.glazunov.data.habits.local.models.LocalHabit
import ru.glazunov.domain.models.Habit
import ru.glazunov.domain.models.HabitPriority
import ru.glazunov.domain.models.HabitType
import java.util.Date

class HabitMapping {
    companion object{
        fun map(habit: Habit): LocalHabit = LocalHabit(
            title = habit.title,
            description = habit.description,
            type = habit.type.value,
            priority = habit.priority.value,
            repeatsCount = habit.count,
            daysPeriod = habit.frequency,
            color = habit.color,
            id = habit.id,
            doneDates = habit.doneDates.map { it.time }.toMutableList()
        )

        fun map(habit: LocalHabit): Habit = Habit(
            title = habit.title,
            description = habit.description,
            type = HabitType.fromInt(habit.type),
            priority = HabitPriority.fromInt(habit.priority),
            count = habit.repeatsCount,
            frequency = habit.daysPeriod,
            color = habit.color,
            id = habit.id,
            doneDates = habit.doneDates?.map { Date(it) } ?: listOf()
        )
    }
}