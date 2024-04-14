package ru.glazunov.habitstracker.models

import android.graphics.Color
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import ru.glazunov.habitstracker.repository.UUIDConverter
import java.util.*

@Parcelize
@Entity
@TypeConverters(UUIDConverter::class)
data class HabitInfo(
    var name: String = "",
    var description: String = "",
    var type: HabitType = HabitType.POSITIVE,
    var priority: String = "",
    var repeatsCount: Int = 0,
    var daysPeriod: Int = 0,
    var color: Int = Color.WHITE,
    @PrimaryKey(autoGenerate = false) var id: UUID = UUID.randomUUID()
): Parcelable