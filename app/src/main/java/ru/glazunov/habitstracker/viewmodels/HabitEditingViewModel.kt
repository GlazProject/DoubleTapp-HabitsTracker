package ru.glazunov.habitstracker.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.repository.HabitsDatabase
import java.util.*
import kotlin.coroutines.CoroutineContext

class HabitEditingViewModel(context: Context) : ViewModel(), CoroutineScope {
    private val job = SupervisorJob()
    private val model = HabitsDatabase.getInstance(context).habitsDao()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job + CoroutineExceptionHandler { _, e -> throw e }

    // При закрытии мы хотим дождаться записи в базу данных, поэтому не вызываем
    // coroutineContext.cancelChildren() в onCleared()

    suspend fun getHabit(id: UUID) = withContext(Dispatchers.Default) {
        model.getHabit(id)
    }


    suspend fun changeHabit(habitInfo: HabitInfo) = withContext(Dispatchers.Default) {
        model.putHabit(habitInfo)
    }
}