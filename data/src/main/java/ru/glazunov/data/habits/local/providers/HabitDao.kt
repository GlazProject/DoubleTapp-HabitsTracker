package ru.glazunov.data.habits.local.providers

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.glazunov.data.habits.local.models.LocalHabit

@Dao
interface HabitDao {
    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putHabit(habit: LocalHabit)

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabit(id: String) : LocalHabit?

    @Query("SELECT * FROM habits " +
            "WHERE type = :habitType " +
            "AND title LIKE :name " +
            "ORDER BY title DESC"
    )
    fun getHabitsByTypeAndPrefixDesc(habitType: Int, name: String = "%"): Flow<List<LocalHabit>>

    @Query("SELECT * FROM habits " +
            "WHERE type = :habitType " +
            "AND title LIKE :name " +
            "ORDER BY title"
    )
    fun getHabitsByTypeAndPrefixAsc(habitType: Int, name: String = "%"): Flow<List<LocalHabit>>
}