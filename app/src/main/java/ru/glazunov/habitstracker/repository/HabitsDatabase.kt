package ru.glazunov.habitstracker.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.glazunov.habitstracker.models.Habit


@Database(entities = [Habit::class], version = 1)
abstract class HabitsDatabase : RoomDatabase() {
    companion object {
        private var instance: HabitsDatabase? = null
        fun getInstance(context: Context): HabitsDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitsDatabase::class.java,
                    "HabitsDatabase"
                )
                    .allowMainThreadQueries()
                    .build()

            return instance!!
        }
    }

    abstract fun habitDao(): HabitDao
}