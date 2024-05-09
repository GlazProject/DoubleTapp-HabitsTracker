package ru.glazunov.habitstracker.data.habits

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import ru.glazunov.habitstracker.data.habits.local.HabitsDatabase
import ru.glazunov.habitstracker.data.habits.remote.RemoteHabitRepository
import ru.glazunov.habitstracker.data.habits.remote.mapping.HabitMapping
import ru.glazunov.habitstracker.data.habits.remote.models.Uid
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering
import java.util.UUID
import ru.glazunov.habitstracker.data.habits.remote.models.NetworkHabit
import ru.glazunov.habitstracker.models.LocalHabit

class HabitsRepository(context: Context): IHabitsRepository {
    private val localHabitsDao = HabitsDatabase.getInstance(context).habitDao()
    private val habitsApi = RemoteHabitRepository.getInstance()

    override suspend fun putHabit(habit: LocalHabit) {
        Log.d("HabitRepo.putHabit", "Putting habit: $habit")
        localHabitsDao.putHabit(habit)
        syncHabit(habit)
    }

    override fun getHabit(id: UUID): LiveData<LocalHabit> = localHabitsDao.getHabit(id.toString())

    override fun getHabits(
        habitType: HabitType,
        namePrefix: String,
        ordering: Ordering
    ): LiveData<List<LocalHabit>> =
        localHabitsDao.getHabitsByTypeAndPrefix(habitType, namePrefix, ordering)

    // network

    private suspend fun getHabitsFromNetwork(): List<NetworkHabit>? = withContext(IO) {
        Log.d("HabitRepo.network", "Fetching habits from network")
        sendRequest { habitsApi.getHabits() }
    }

    private suspend fun putHabitToNetwork(habit: NetworkHabit): Uid? = withContext(IO) {
        Log.d("HabitRepo.network", "Sending habit to network: $habit")
        sendRequest { habitsApi.putHabit(habit) }
    }

    private suspend fun <T> sendRequest(method: suspend () -> Response<T>): T? {
        val response: Response<T>
        try {
            response = method()
        } catch (ex: Exception) {
            Log.e(
                "HabitRepo.network",
                "Unable to get response due to $ex. \r\n${ex.stackTraceToString()}"
            )
            return null
        }

        if (response.code() == 200) {
            Log.d("HabitRepo.network", "Request successful: ${response.body()}")
            return response.body()
        }
        return null
    }

    private suspend fun syncHabit(habit: LocalHabit) {
        if (!habit.isModified) return
        Log.d("HabitRepo.syncHabit", "Synchronizing habit: $habit")
        val newId = putHabitToNetwork(HabitMapping.map(habit)) ?: return
        if (habit.isLocal) {
            localHabitsDao.deleteHabit(habit.id.toString())
            habit.id = UUID.fromString(newId.uid)
            habit.isLocal = false
            habit.isModified = false
            localHabitsDao.putHabit(habit)
        }
        Log.d("HabitRepo.syncHabit", "Habit synchronized successfully")
    }

    override suspend fun syncHabits(scope: CoroutineScope) {
        getHabitsFromNetwork()?.forEach { habit ->
            localHabitsDao.putHabit(HabitMapping.map(habit))
        }
        localHabitsDao.getHabits().value?.forEach{ habit ->
            scope.launch(IO){ syncHabit(habit) }}
    }

    companion object {
        private var instance: IHabitsRepository? = null
        fun getInstance(context: Context): IHabitsRepository {
            if (instance == null) {
                instance = HabitsRepository(context)
            }
            return instance!!
        }
    }
}