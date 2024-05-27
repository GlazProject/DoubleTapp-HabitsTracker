package ru.glazunov.data.habits.remote.models

import java.io.Serializable

data class NetworkHabit (
    val color: Int? = null,
    val count: Int? = null,
    val date: Int? = null,
    val description: String? = null,
    val done_dates: List<Long>? = null,
    val frequency: Int? = null,
    val priority: Int? = null,
    val title: String? = null,
    val type: Int? = null,
    val uid: String? = null,
): Serializable

