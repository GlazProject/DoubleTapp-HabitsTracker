package ru.glazunov.habitstracker.models

enum class HabitType(val value: Int) {
    POSITIVE(0),
    NEGATIVE(1);

    companion object{
        fun fromInt(value: Int): HabitType = when (value){
            0 -> POSITIVE
            1 -> NEGATIVE
            else -> throw IllegalArgumentException("Unexpected value $value")
        }
    }
}