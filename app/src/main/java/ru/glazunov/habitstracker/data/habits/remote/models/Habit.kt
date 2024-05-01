package ru.glazunov.habitstracker.data.habits.remote.models

import java.io.Serializable

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
): Serializable

