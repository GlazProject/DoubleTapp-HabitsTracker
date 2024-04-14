package ru.glazunov.habitstracker.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.glazunov.habitstracker.models.HabitInfo

@Database(entities = [HabitInfo::class], version = 1)
abstract class HabitsDatabase : RoomDatabase() {
    abstract fun habitsDao(): HabitsDao
}