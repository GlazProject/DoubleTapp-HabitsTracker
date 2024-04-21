package ru.glazunov.habitstracker.viewmodels

import android.os.Bundle
import android.util.Log
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
import ru.glazunov.habitstracker.repository.HabitDao

class HabitsListViewModel(
    dao: HabitDao,
    lifecycleOwner: LifecycleOwner
): ViewModel() {
    private val state = HabitListViewModelState()
    private val positiveProcessedHabits: MutableLiveData<List<Habit>> = MutableLiveData()
    private val negativeProcessedHabits: MutableLiveData<List<Habit>> = MutableLiveData()
    private var baseHabits: List<Habit> = arrayListOf()

    init {
        dao.getAllHabits().observe(lifecycleOwner) { habits ->
            baseHabits = habits
            selectByState()
        }
    }

    fun habits(type: HabitType): LiveData<List<Habit>> =
        when (type) {
            HabitType.NEGATIVE -> negativeProcessedHabits
            HabitType.POSITIVE -> positiveProcessedHabits
        }

    fun selectByName(name: String){
        state.searchPrefix = name
        selectByState()
    }

    fun orderByName(ordering: Ordering){
        state.ordering = ordering
        selectByState()
    }

    private fun selectByNamePrefixInternal(name: String, data: MutableLiveData<List<Habit>>) {
        if (name.isEmpty() || name.isBlank())
            return

        data.value = data.value?.filter { habit -> habit.name.startsWith(name, true) }
                ?.let { ArrayList(it) }
        Log.d(this::class.java.canonicalName, "Was selected by name $name")
    }

    // TODO Лучше обращаться к БД, чем сортировать у себя
    private fun orderByNameInternal(ordering: Ordering, data: MutableLiveData<List<Habit>>) {
        data.value = when(ordering){
            Ordering.Ascending -> data.value?.sortedBy { it.name }?.let { ArrayList(it) }
            Ordering.Descending -> data.value?.sortedByDescending { it.name }?.let { ArrayList(it) }
        }
        Log.d(this::class.java.canonicalName, "Was ordered by $ordering")
    }

    private fun selectByState(){
        positiveProcessedHabits.value =baseHabits.filter { habit -> habit.type == HabitType.POSITIVE }
        negativeProcessedHabits.value = baseHabits.filter { habit -> habit.type == HabitType.NEGATIVE }

        selectByNamePrefixInternal(state.searchPrefix, positiveProcessedHabits)
        selectByNamePrefixInternal(state.searchPrefix, negativeProcessedHabits)
        orderByNameInternal(state.ordering, positiveProcessedHabits)
        orderByNameInternal(state.ordering, negativeProcessedHabits)
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