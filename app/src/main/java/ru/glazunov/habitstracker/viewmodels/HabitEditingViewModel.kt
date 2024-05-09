package ru.glazunov.habitstracker.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import ru.glazunov.habitstracker.data.habits.IHabitsRepository
import ru.glazunov.habitstracker.models.LocalHabit
import java.util.*

class HabitEditingViewModel(
    private val owner: LifecycleOwner,
    private val repo: IHabitsRepository
) : ViewModel() {
    var habit: LocalHabit = LocalHabit()

    fun getHabit(id: UUID): LiveData<LocalHabit> =
        repo.getHabit(id).also { it.observe(owner){ habit -> this.habit = habit }}

    fun saveHabit() = viewModelScope.launch(IO) {
        habit.isModified = true
        repo.putHabit(habit)
    }

    companion object {
        private var instance: HabitEditingViewModel? = null

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            repo: IHabitsRepository,
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
                        instance = HabitEditingViewModel(owner, repo)
                    return instance!! as T
                }
            }
    }
}