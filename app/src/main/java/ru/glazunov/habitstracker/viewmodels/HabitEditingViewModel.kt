package ru.glazunov.habitstracker.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import ru.glazunov.habitstracker.models.Habit
import ru.glazunov.habitstracker.repository.local.HabitDao
import java.util.*
import kotlin.coroutines.CoroutineContext

class HabitEditingViewModel(
    private val owner: LifecycleOwner,
    private val dao: HabitDao) : ViewModel() {
    var habit: Habit = Habit()

    fun getHabit(id: UUID): LiveData<Habit> =
        dao.getHabit(id.toString()).also { it.observe(owner){ habit -> this.habit = habit} }

    fun saveHabit() = viewModelScope.launch(IO) {
        dao.putHabit(habit)
    }

    companion object {
        private var instance: HabitEditingViewModel? = null

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            dao: HabitDao,
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
                        instance = HabitEditingViewModel(owner, dao)
                    return instance!! as T
                }
            }
    }
}