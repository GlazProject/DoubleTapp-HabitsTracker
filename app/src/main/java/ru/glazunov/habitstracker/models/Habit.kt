package ru.glazunov.habitstracker.models

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.glazunov.habitstracker.repository.UUIDConverter
import java.util.*

@Entity(tableName = "habits")
@TypeConverters(UUIDConverter::class)
data class Habit(
    var name: String = "",
    var description: String = "",
    var type: HabitType = HabitType.POSITIVE,
    var priority: String = "",
    var repeatsCount: Int = 0,
    var daysPeriod: Int = 0,
    var color: Int = Color.WHITE,
    @PrimaryKey(autoGenerate = false) var id: UUID = UUID.randomUUID()
)