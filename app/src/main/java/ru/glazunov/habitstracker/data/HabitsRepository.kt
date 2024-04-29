package ru.glazunov.habitstracker.data

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.data.local.HabitsDatabase
import ru.glazunov.habitstracker.data.remote.RemoteHabitRepository
import ru.glazunov.habitstracker.data.remote.mapping.HabitMapping
import ru.glazunov.habitstracker.data.remote.models.Uid
import ru.glazunov.habitstracker.data.remote.models.Habit as NetworkHabit
import ru.glazunov.habitstracker.models.Habit as LocalHabit
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering
import java.util.UUID

class HabitsRepository(private val context: Context, private val lifecycleOwner: LifecycleOwner) :
    IHabitsRepository {

    private val localHabitsDao = HabitsDatabase.getInstance(context).habitDao()
    private val habitsApi = RemoteHabitRepository.getInstance()

    private val logCoroutineExceptionHandler = CoroutineExceptionHandler{ _, e -> e.printStackTrace() }
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    init {
        lifecycleOwner.lifecycleScope.launch(IO + logCoroutineExceptionHandler) { syncHabits() }
    }

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
         for (retryNumber in 0..MAX_RETRIES) {
            var response: Response<T>
            if (connectivityManager.isDefaultNetworkActive) {
                Log.d(
                    "HabitRepo.network",
                    "Sending request to remote server (attempt ${retryNumber + 1})"
                )
                try {
                    response = method()
                }
                catch (ex: Exception){
                    Log.e("HabitRepo.network", "Unable to get response due to $ex. \r\n${ex.stackTraceToString()}")
                    break
                }

                if (response.code() == 200) {
                    Log.d("HabitRepo.network", "Request successful: ${response.body()}")
                    return response.body()
                }

                if (response.code() in arrayOf(400, 401, 403, 500)) {
                    Log.e(
                        "HabitRepo.network",
                        "Can not get response from server due to code ${response.code()}. ${response.errorBody()?.string()}"
                    )
                    break
                }

                Log.e(
                    "HabitRepo.network",
                    response.message() ?: "Response is null"
                )
            } else
                Log.d("HabitRepo.network", "No network connection, retrying in ${RETRY_TIMEOUT}ms")
             delay(RETRY_TIMEOUT)
        }

        Log.e("HabitRepo.network", "Failed to fetch habits after $MAX_RETRIES retries")

        withContext(Dispatchers.Main) {
            Toast.makeText(context, R.string.unable_to_sync_habit, Toast.LENGTH_SHORT).show()
        }

        return null
    }

    // synchronisation

    private suspend fun syncHabit(habit: LocalHabit) {
        if (!habit.isModified) return
        Log.d("HabitRepo.syncHabit", "Synchronizing habit: $habit")
        val newId = putHabitToNetwork(HabitMapping.map(habit)) ?: return
        localHabitsDao.deleteHabit(habit.id.toString())
        habit.id = UUID.fromString(newId.uid)
        habit.isLocal = false
        habit.isModified = false
        localHabitsDao.putHabit(habit)
        Log.d("HabitRepo.syncHabit", "Habit synchronized successfully")
    }

    override suspend fun syncHabits() {
        getHabitsFromNetwork()?.forEach { habit ->
            localHabitsDao.putHabit(HabitMapping.map(habit))
        }
        localHabitsDao.getHabits().value?.forEach{ habit ->
            lifecycleOwner.lifecycleScope.launch(IO + logCoroutineExceptionHandler){ syncHabit(habit) }}
    }

    companion object {
        private const val MAX_RETRIES: Int = 10
        private const val RETRY_TIMEOUT: Long = 1000

        private var instance: IHabitsRepository? = null
        fun getInstance(context: Context, lifecycleOwner: LifecycleOwner): IHabitsRepository {
            if (instance == null) {
                instance = HabitsRepository(context, lifecycleOwner)
            }
            return instance!!
        }
    }
}