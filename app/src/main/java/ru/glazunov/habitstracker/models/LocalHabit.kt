package ru.glazunov.habitstracker.models

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.glazunov.habitstracker.data.habits.local.UUIDConverter
import java.util.*

@Entity(tableName = "habits")
@TypeConverters(UUIDConverter::class)
data class LocalHabit(
    var name: String = "",
    var description: String = "",
    var type: HabitType = HabitType.POSITIVE,
    var priority: HabitPriority = HabitPriority.LOW,
    var repeatsCount: Int = 0,
    var daysPeriod: Int = 0,
    var color: Int = Color.WHITE,
    var networkId: String? = null,
    var modified: Boolean = true,
    @PrimaryKey(autoGenerate = false) var id: UUID = UUID.randomUUID()
)