package ru.glazunov.habitstracker.data.habits.remote.mapping

import android.graphics.Color
import ru.glazunov.habitstracker.models.HabitPriority
import ru.glazunov.habitstracker.models.HabitType
import java.time.Clock
import java.util.UUID
import ru.glazunov.habitstracker.data.habits.remote.models.Habit as NetworkHabit
import ru.glazunov.habitstracker.models.Habit as LocalHabit

class HabitMapping {
    companion object{
        fun map(habit: NetworkHabit): LocalHabit = LocalHabit(
            name = habit.title ?: "",
            description = habit.description ?: "",
            type = HabitType.fromInt(habit.type ?: 0),
            priority = HabitPriority.fromInt(habit.priority ?: 0),
            repeatsCount = habit.count ?: 0,
            daysPeriod = habit.frequency ?: 0,
            color = habit.color ?: Color.WHITE,
            id = UUID.fromString(habit.uid)
        )

        fun map(habit: LocalHabit): NetworkHabit = NetworkHabit(
            color = habit.color,
            count = habit.repeatsCount,
            description = habit.description,
            frequency = habit.daysPeriod,
            priority = habit.priority.value,
            title = habit.name,
            type = habit.type.value,
            date = Clock.systemUTC().millis().toInt(),
            uid = if (habit.isLocal) "" else habit.id.toString()
        )
    }
}