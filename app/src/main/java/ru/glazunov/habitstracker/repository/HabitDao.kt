package ru.glazunov.habitstracker.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.glazunov.habitstracker.models.Habit
import ru.glazunov.habitstracker.models.HabitType

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): LiveData<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putHabit(habit: Habit)

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabit(id: String) : LiveData<Habit>

    @Query("SELECT * FROM habits WHERE type = :habitType")
    fun getHabitsByType(habitType: HabitType): LiveData<List<Habit>>
}