package ru.glazunov.habitstracker.domain.models

data class DoneHabitResult (
    val type: DoneHabitResultType,
    val value: Int? = null
)