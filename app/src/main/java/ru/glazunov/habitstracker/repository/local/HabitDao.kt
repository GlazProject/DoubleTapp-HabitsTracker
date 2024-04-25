package ru.glazunov.habitstracker.repository.local

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.glazunov.habitstracker.models.Habit
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putHabit(habit: Habit)

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabit(id: String) : LiveData<Habit>

    @Query("SELECT * FROM habits WHERE type = :habitType")
    fun getHabitsByType(habitType: HabitType): LiveData<List<Habit>>

    @Query("SELECT * FROM habits " +
            "WHERE type = :habitType " +
            "AND name LIKE :name " +
            "ORDER BY  name DESC")
    fun getHabitsByTypeAndPrefixDesc(habitType: HabitType, name: String = "%"): LiveData<List<Habit>>

    @Query("SELECT * FROM habits " +
            "WHERE type = :habitType " +
            "AND name LIKE :name " +
            "ORDER BY  name")
    fun getHabitsByTypeAndPrefixAsc(habitType: HabitType, name: String = "%"): LiveData<List<Habit>>

    fun getHabitsByTypeAndPrefix(habitType: HabitType,
                                 namePrefix: String = "",
                                 ordering: Ordering = Ordering.Ascending): LiveData<List<Habit>> =
        when(ordering){
            Ordering.Ascending -> getHabitsByTypeAndPrefixAsc(habitType, "$namePrefix%")
            Ordering.Descending -> getHabitsByTypeAndPrefixDesc(habitType, "$namePrefix%")
        }
}