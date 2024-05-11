package ru.glazunov.habitstracker.data.habits.synchronization

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.glazunov.habitstracker.data.habits.synchronization.models.HabitState
import ru.glazunov.habitstracker.data.habits.synchronization.providers.HabitSyncDao

@Database(entities = [HabitState::class], version = 1)
abstract class HabitsSyncDatabase: RoomDatabase() {
    companion object {
        private var instance: HabitsSyncDatabase? = null
        fun getInstance(context: Context): HabitsSyncDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitsSyncDatabase::class.java,
                    "HabitsSyncDatabase"
                )
                    .allowMainThreadQueries()
                    .build()

            return instance!!
        }
    }

    abstract fun getDao(): HabitSyncDao
}