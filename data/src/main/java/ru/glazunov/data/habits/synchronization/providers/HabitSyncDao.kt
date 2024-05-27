package ru.glazunov.data.habits.synchronization.providers

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.glazunov.data.habits.synchronization.models.HabitState
import ru.glazunov.data.habits.synchronization.models.HabitStatus

@Dao
interface HabitSyncDao {
    @Query("DELETE FROM habitsState WHERE habitId = :id")
    suspend fun deleteRecord(id: String)

    @Query("SELECT * FROM habitsState WHERE habitId = :id")
    suspend fun getRecord(id: String): HabitState?

    @Query("SELECT * FROM habitsState WHERE networkId = :id")
    suspend fun getRecordByNetworkId(id: String): HabitState?

    @Query("SELECT * FROM habitsState WHERE status != 'Deleted'")
    suspend fun getNotDeleted(): List<HabitState>

    @Query("SELECT * FROM habitsState WHERE status = :status")
    fun getByStatus(status: HabitStatus): Flow<List<HabitState>>

    fun getModified(): Flow<List<HabitState>> = getByStatus(HabitStatus.Modified)
    fun getDeleted(): Flow<List<HabitState>> = getByStatus(HabitStatus.Deleted)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(state: HabitState)
}