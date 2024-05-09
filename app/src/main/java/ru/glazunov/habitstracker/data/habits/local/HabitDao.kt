package ru.glazunov.habitstracker.data.habits.local

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.glazunov.habitstracker.models.LocalHabit
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering

@Dao
interface HabitDao {
    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putHabit(habit: LocalHabit)

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabit(id: String) : LiveData<LocalHabit>

    @Query("SELECT * FROM habits")
    fun getHabits(): LiveData<List<LocalHabit>>

    @Query("SELECT * FROM habits WHERE type = :habitType")
    fun getHabitsByType(habitType: HabitType): LiveData<List<LocalHabit>>

    @Query("SELECT * FROM habits " +
            "WHERE type = :habitType " +
            "AND name LIKE :name " +
            "ORDER BY  name DESC")
    fun getHabitsByTypeAndPrefixDesc(habitType: HabitType, name: String = "%"): LiveData<List<LocalHabit>>

    @Query("SELECT * FROM habits " +
            "WHERE type = :habitType " +
            "AND name LIKE :name " +
            "ORDER BY  name")
    fun getHabitsByTypeAndPrefixAsc(habitType: HabitType, name: String = "%"): LiveData<List<LocalHabit>>

    fun getHabitsByTypeAndPrefix(habitType: HabitType,
                                 namePrefix: String = "",
                                 ordering: Ordering = Ordering.Ascending): LiveData<List<LocalHabit>> =
        when(ordering){
            Ordering.Ascending -> getHabitsByTypeAndPrefixAsc(habitType, "$namePrefix%")
            Ordering.Descending -> getHabitsByTypeAndPrefixDesc(habitType, "$namePrefix%")
        }
}