package ru.glazunov.data.habits.remote.mapping

import ru.glazunov.data.habits.remote.models.NetworkHabit
import ru.glazunov.domain.models.Habit
import ru.glazunov.domain.models.HabitPriority
import ru.glazunov.domain.models.HabitType
import java.util.Date

class HabitMapping{
    companion object{
        fun map(habit: Habit): NetworkHabit = NetworkHabit(
            color = habit.color,
            count = habit.count,
            description = habit.description,
            frequency = habit.frequency,
            priority = habit.priority.value,
            title = habit.title,
            type = habit.type.value,
            date = Date().time.toInt(),
            uid = habit.remoteId ?: ""
        )

        fun map(habit: NetworkHabit): Habit = Habit(
            title = habit.title ?: "",
            description = habit.description ?: "",
            type = HabitType.fromInt(habit.type ?: 0),
            priority = HabitPriority.fromInt(habit.priority ?: 0),
            count = habit.count ?: 0,
            frequency = habit.frequency ?: 0,
            color = habit.color ?: -1,
            doneDates = habit.done_dates?.map { Date(it) } ?: listOf(),
            remoteId = habit.uid
        )
    }
}