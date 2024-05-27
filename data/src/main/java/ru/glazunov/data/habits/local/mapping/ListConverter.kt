package ru.glazunov.data.habits.local.mapping

import androidx.room.TypeConverter

class ListConverter {
    @TypeConverter
    fun fromList(list: List<Long>?) : String? = list?.joinToString(",")

    @TypeConverter
    fun toList(string: String?): List<Long>? =
        string?.split(",")?.filter { it.isNotEmpty() }?.map { it.toLong() }
}