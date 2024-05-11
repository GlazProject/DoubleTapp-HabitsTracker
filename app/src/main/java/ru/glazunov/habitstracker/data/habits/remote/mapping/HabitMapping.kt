package ru.glazunov.habitstracker.data.habits.remote.mapping

import ru.glazunov.habitstracker.data.habits.remote.models.NetworkHabit
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitPriority
import ru.glazunov.habitstracker.domain.models.HabitType
import java.time.Clock

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
            date = Clock.systemUTC().millis().toInt(),
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
            remoteId = habit.uid
        )
    }
}