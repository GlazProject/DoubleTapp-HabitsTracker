package ru.glazunov.habitstracker.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.models.HabitType
import java.util.*

@Dao
interface HabitsDao : IHabitsRepository {
    @Query("SELECT * FROM HabitInfo")
    override fun getHabits(): LiveData<List<HabitInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun putHabit(habitInfo: HabitInfo)

    @Query("SELECT * FROM HabitInfo WHERE id = :id")
    override fun getHabit(id: UUID) : HabitInfo

    override fun getPositiveHabits() = getHabitsByType(HabitType.POSITIVE)

    override fun getNegativeHabits() = getHabitsByType(HabitType.NEGATIVE)

    @Query("SELECT * FROM HabitInfo WHERE type = :habitType")
    fun getHabitsByType(habitType: HabitType): LiveData<List<HabitInfo>>
}