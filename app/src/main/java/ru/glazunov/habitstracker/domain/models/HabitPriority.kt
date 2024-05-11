package ru.glazunov.habitstracker.domain.models

enum class HabitPriority(val value: Int) {
    LOW(0),
    MEDIUM(1),
    HIGH(2);

    companion object{
        fun fromInt(value: Int): HabitPriority = when (value){
            0 -> LOW
            1 -> MEDIUM
            2 -> HIGH
            else -> throw IllegalArgumentException("Unexpected value $value")
        }
    }
}