package ru.glazunov.habitstracker.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import ru.glazunov.habitstracker.models.Habit
import ru.glazunov.habitstracker.models.HabitListViewModelState
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering
import ru.glazunov.habitstracker.repository.local.HabitDao

class HabitsListViewModel(
    private val dao: HabitDao,
    private val lifecycleOwner: LifecycleOwner
): ViewModel() {
    private val state = HabitListViewModelState()
    private val positiveHabits: MutableLiveData<List<Habit>> = MutableLiveData()
    private val negativeHabits: MutableLiveData<List<Habit>> = MutableLiveData()

    init {
        selectByState()
    }

    fun habits(type: HabitType): LiveData<List<Habit>> =
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
        dao.getHabitsByTypeAndPrefix(HabitType.POSITIVE, state.searchPrefix, state.ordering)
            .observe(lifecycleOwner) { habits ->
            positiveHabits.value = habits
        }
        dao.getHabitsByTypeAndPrefix(HabitType.NEGATIVE, state.searchPrefix, state.ordering)
            .observe(lifecycleOwner) { habits ->
            negativeHabits.value = habits
        }
    }

    companion object {
        private var instance: HabitsListViewModel? = null

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            dao: HabitDao,
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
                        instance = HabitsListViewModel(dao, lifecycleOwner)
                    return instance!! as T
                }
            }
    }
}