package ru.glazunov.habitstracker.data.habits

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers.IO
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

    override suspend fun deleteHabit(habit: LocalHabit) {
        Log.d("HabitRepo.deleteHabit", "Deleting habit: $habit")
        localHabitsDao.deleteHabit(habit.id.toString())
        deleteHabitFromNetwork(habit)
    }

    override fun getHabit(id: UUID): LiveData<LocalHabit> {
        Log.d("HabitRepo.getHabit", "Send get request to DB for id: $id")
        return localHabitsDao.getHabit(id.toString())
    }

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

    private suspend fun deleteHabitFromNetwork(habit: LocalHabit) = withContext(IO) {
        Log.d("HabitRepo.network", "Delete habit from network: $habit")
        sendRequest { habitsApi.deleteHabit(Uid(uid = habit.networkId)) }
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
        if (!habit.modified) return
        val newId = putHabitToNetwork(HabitMapping.map(habit)) ?: return
        habit.networkId = newId.uid
        habit.modified = false
        localHabitsDao.putHabit(habit)
        Log.d("HabitRepo.syncHabit", "Habit synchronized successfully")
    }

    override suspend fun syncHabits() {
        val localHabits = localHabitsDao.getHabits()
        val networkHabits = getHabitsFromNetwork()

        for (habit in networkHabits?: listOf()) {
            val localHabit = localHabits.firstOrNull { it.networkId == habit.uid }
            localHabitsDao.putHabit(
                HabitMapping.map(habit, localHabit?.id ?: UUID.randomUUID())
            )
        }

        localHabits.filter { it.networkId != null }.forEach{habit ->
            if (networkHabits?.firstOrNull{it.uid == habit.networkId} != null)
                syncHabit(habit)
            else
                localHabitsDao.deleteHabit(habit.id.toString())
        }

        localHabits.filter { it.networkId == null }.forEach{syncHabit(it)}
    }

    companion object {
        private var instance: IHabitsRepository? = null
        fun getInstance(context: Context): IHabitsRepository {
            if (instance == null) instance = HabitsRepository(context)
            return instance!!
        }
    }
}