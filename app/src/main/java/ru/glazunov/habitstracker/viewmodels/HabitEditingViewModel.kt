package ru.glazunov.habitstracker.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.repository.IHabitsRepository
import java.util.*
import kotlin.coroutines.CoroutineContext

class HabitEditingViewModel(private val model: IHabitsRepository) : ViewModel(), CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job + CoroutineExceptionHandler { _, e -> throw e }

    // При закрытии мы хотим дождаться записи в базу данных, поэтому не вызываем
    // coroutineContext.cancelChildren() в onCleared()

    suspend fun getHabit(id: UUID) = withContext(Dispatchers.Main) {
        model.getHabit(id)
    }


    fun changeHabit(habitInfo: HabitInfo) = launch{ withContext(Dispatchers.Default) {
        model.putHabit(habitInfo)
    }}

    companion object {
        private var instance: HabitEditingViewModel? = null

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            repository: IHabitsRepository,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    if (instance == null)
                        instance = HabitEditingViewModel(repository)
                    return instance!! as T
                }
            }
    }
}