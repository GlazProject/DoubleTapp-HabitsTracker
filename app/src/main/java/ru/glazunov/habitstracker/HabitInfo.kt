package ru.glazunov.habitstracker

import android.graphics.Color
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class HabitInfo(
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val priority: String = "",
    val repeatsCount: Int = 0,
    val daysPeriod: Int = 0,
    val color: Int = Color.WHITE
): Parcelable