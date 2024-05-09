package ru.glazunov.habitstracker.data.habits.local

import androidx.room.TypeConverter
import java.util.*

class UUIDConverter{
    @TypeConverter
    fun fromUUID(uuid: UUID?) : String? = uuid?.toString()

    @TypeConverter
    fun toUUID(string: String?): UUID? =
        if (string == null) null else UUID.fromString(string)
}