package ru.glazunov.habitstracker.data.habits.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.glazunov.habitstracker.data.habits.local.providers.HabitDao
import ru.glazunov.habitstracker.data.habits.local.models.LocalHabit

@Database(entities = [LocalHabit::class], version = 1)
abstract class HabitsDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}