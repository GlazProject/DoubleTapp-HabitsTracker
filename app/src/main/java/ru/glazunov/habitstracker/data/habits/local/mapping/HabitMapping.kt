package ru.glazunov.habitstracker.data.habits.local.mapping

import ru.glazunov.habitstracker.data.habits.local.models.LocalHabit
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitPriority
import ru.glazunov.habitstracker.domain.models.HabitType

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
            id = habit.id
        )

        fun map(habit: LocalHabit): Habit = Habit(
            title = habit.title,
            description = habit.description,
            type = HabitType.fromInt(habit.type),
            priority = HabitPriority.fromInt(habit.priority),
            count = habit.repeatsCount,
            frequency = habit.daysPeriod,
            color = habit.color,
            id = habit.id
        )
    }
}