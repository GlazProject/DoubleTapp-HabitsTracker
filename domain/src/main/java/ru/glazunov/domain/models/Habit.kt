package ru.glazunov.domain.models

import java.util.Date
import java.util.UUID

data class Habit (
    var title: String = "",
    var description: String = "",
    var type: HabitType = HabitType.POSITIVE,
    var priority: HabitPriority = HabitPriority.HIGH,
    var count: Int = 0,
    var frequency: Int = 0,
    var color: Int = -1,
    var remoteId: String? = null,
    var doneDates: List<Date> = listOf(),
    var id: UUID = UUID.randomUUID()
)