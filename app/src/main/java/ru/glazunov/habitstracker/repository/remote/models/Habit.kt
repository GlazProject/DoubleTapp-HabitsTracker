package ru.glazunov.habitstracker.repository.remote.models


data class Habit (
    val color: Int? = null,
    val count: Int? = null,
    val date: Int? = null,
    val description: String? = null,
    val doneDates: Array<Int>? = null,
    val frequency: Int? = null,
    val priority: Int? = null,
    val title: String? = null,
    val type: Int? = null,
    val uid: String? = null,
)

