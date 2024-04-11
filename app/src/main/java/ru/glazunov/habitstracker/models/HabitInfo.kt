package ru.glazunov.habitstracker.models

import android.graphics.Color
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.lang.reflect.Type
import java.util.*

@Parcelize
class HabitInfo(
    val name: String = "",
    val description: String = "",
    val type: HabitType = HabitType.POSITIVE,
    val priority: String = "",
    val repeatsCount: Int = 0,
    val daysPeriod: Int = 0,
    val color: Int = Color.WHITE,
    val id: UUID = UUID.randomUUID()
): Parcelable