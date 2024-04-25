package ru.glazunov.habitstracker.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.launch
import ru.glazunov.habitstracker.models.Habit
import ru.glazunov.habitstracker.repository.local.HabitDao
import java.util.*

class HabitEditingViewModel(private val dao: HabitDao) : ViewModel() {
    fun getHabit(id: UUID) = dao.getHabit(id.toString())

    fun changeHabit(habit: Habit) = viewModelScope.launch {
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
                        instance = HabitEditingViewModel(dao)
                    return instance!! as T
                }
            }
    }
}