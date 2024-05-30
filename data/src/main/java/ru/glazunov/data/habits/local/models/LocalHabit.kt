package ru.glazunov.data.habits.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.glazunov.data.habits.local.mapping.ListConverter
import ru.glazunov.data.habits.local.mapping.UUIDConverter
import java.util.*

@Entity(tableName = "habits")
@TypeConverters(UUIDConverter::class, ListConverter::class)
data class LocalHabit(
    @PrimaryKey(autoGenerate = false) var id: UUID = UUID.randomUUID(),
    var title: String = "",
    var description: String = "",
    var type: Int = 0,
    var priority: Int = 0,
    var repeatsCount: Int = 0,
    var daysPeriod: Int = 0,
    var color: Int = -1,
    var doneDates: MutableList<Long>? = null
)