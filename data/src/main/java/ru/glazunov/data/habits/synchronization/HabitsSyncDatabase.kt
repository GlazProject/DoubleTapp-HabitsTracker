package ru.glazunov.data.habits.synchronization

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.glazunov.data.habits.synchronization.models.HabitState
import ru.glazunov.data.habits.synchronization.providers.HabitSyncDao

@Database(entities = [HabitState::class], version = 1)
abstract class HabitsSyncDatabase: RoomDatabase() {
    abstract fun getDao(): HabitSyncDao
}