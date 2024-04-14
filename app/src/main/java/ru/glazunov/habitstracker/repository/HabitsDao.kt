package ru.glazunov.habitstracker.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.glazunov.habitstracker.models.HabitInfo
import java.util.*

@Dao
interface HabitsDao : IHabitsRepository {
    @Query("SELECT * FROM HabitInfo")
    override fun getHabits(): LiveData<List<HabitInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun putHabit(habitInfo: HabitInfo)

    @Query("SELECT * FROM HabitInfo WHERE id = :id")
    override fun getHabit(id: UUID) : HabitInfo

    @Query("SELECT * FROM HabitInfo WHERE type = 'POSITIVE'")
    override fun getPositiveHabits() : LiveData<List<HabitInfo>>

    @Query("SELECT * FROM HabitInfo WHERE type = 'NEGATIVE'")
    override fun getNegativeHabits() : LiveData<List<HabitInfo>>
}