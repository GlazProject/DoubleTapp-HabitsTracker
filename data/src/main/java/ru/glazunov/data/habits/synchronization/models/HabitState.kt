package ru.glazunov.data.habits.synchronization.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.glazunov.data.habits.local.mapping.UUIDConverter
import java.util.UUID

@Entity(tableName = "habitsState")
@TypeConverters(UUIDConverter::class)
data class HabitState (
    @PrimaryKey(autoGenerate = false) val habitId: UUID,
    val networkId: String?,
    val status: HabitStatus,
    val doneTimes: Int
)