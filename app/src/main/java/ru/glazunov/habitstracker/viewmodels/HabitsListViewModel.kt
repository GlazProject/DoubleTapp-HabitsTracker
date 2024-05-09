package ru.glazunov.habitstracker.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import ru.glazunov.habitstracker.data.habits.IHabitsRepository
import ru.glazunov.habitstracker.models.LocalHabit
import ru.glazunov.habitstracker.models.HabitListViewModelState
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering

class HabitsListViewModel(
    private val repo: IHabitsRepository,
    private val lifecycleOwner: LifecycleOwner
): ViewModel() {
    private val state = HabitListViewModelState()
    private val positiveHabits: MutableLiveData<List<LocalHabit>> = MutableLiveData()
    private val negativeHabits: MutableLiveData<List<LocalHabit>> = MutableLiveData()

    init {
        viewModelScope.launch(IO) { repo.syncHabits(viewModelScope)}
        selectByState()
    }

    fun habits(type: HabitType): LiveData<List<LocalHabit>> =
        when (type) {
            HabitType.NEGATIVE -> negativeHabits
            HabitType.POSITIVE -> positiveHabits
        }

    fun selectByName(name: String){
        state.searchPrefix = name
        selectByState()
    }

    fun orderByName(ordering: Ordering){
        state.ordering = ordering
        selectByState()
    }

    private fun selectByState(){
        repo.getHabits(HabitType.POSITIVE, state.searchPrefix, state.ordering)
            .observe(lifecycleOwner) { habits ->
            positiveHabits.value = habits
        }
        repo.getHabits(HabitType.NEGATIVE, state.searchPrefix, state.ordering)
            .observe(lifecycleOwner) { habits ->
            negativeHabits.value = habits
        }
    }

    companion object {
        private var instance: HabitsListViewModel? = null

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            repo: IHabitsRepository,
            lifecycleOwner: LifecycleOwner,
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
                        instance = HabitsListViewModel(repo, lifecycleOwner)
                    return instance!! as T
                }
            }
    }
}