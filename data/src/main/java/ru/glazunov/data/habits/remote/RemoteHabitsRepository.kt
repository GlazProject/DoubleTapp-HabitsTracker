package ru.glazunov.data.habits.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.Response
import ru.glazunov.data.habits.remote.mapping.HabitMapping
import ru.glazunov.data.habits.remote.models.HabitDone
import ru.glazunov.data.habits.remote.models.Uid
import ru.glazunov.domain.models.Habit
import ru.glazunov.domain.repositories.IRemoteHabitsRepository
import java.util.Date

class RemoteHabitsRepository(private val api: HabitsApi)
    : IRemoteHabitsRepository {
    override suspend fun getAll(): List<Habit> =
        withContext(IO) { sendRequest { api.getHabits() } }
            ?.map { HabitMapping.map(it) }
            ?: listOf()

    override suspend fun put(habit: Habit): String? =
        withContext(IO) { sendRequest { api.putHabit(HabitMapping.map(habit)) } }?.uid

    override suspend fun delete(id: String): Boolean =
        withContext(IO) { sendRequest { api.deleteHabit(Uid(id)) } } != null

    override suspend fun done(date: Date, id: String): Boolean =
        withContext(IO) {sendRequest { api.doneHabit(HabitDone(date.time, id)) }} != null


    private suspend fun <T> sendRequest(method: suspend () -> Response<T>): T? {
        val response: Response<T>
        try {
            response = method()
        } catch (ex: Exception) {
            Log.e("RemoteHabitsRepository",
                "Unable to get response due to $ex. \r\n${ex.stackTraceToString()}")
            return null
        }

        if (response.code() == 200) {
            Log.d("RemoteHabitsRepository", "Request successful: ${response.body()}")
            return response.body()
        }
        return null
    }
}