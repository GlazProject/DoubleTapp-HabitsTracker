package ru.glazunov.habitstracker

import ru.glazunov.habitstracker.models.HabitInfo

interface IHabitChangedCallback {
    fun onHabitChanged(
        position: Int?,
        habitInfo: HabitInfo?,
        oldHabitInfo: HabitInfo?,
        oldPosition: Int?
    )
}