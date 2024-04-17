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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.models.HabitListViewModelState
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering
import ru.glazunov.habitstracker.repository.IHabitsRepository

class HabitsListViewModel(
    model: IHabitsRepository,
    lifecycleOwner: LifecycleOwner
): ViewModel() {
    private val state = HabitListViewModelState()
//    private val model = HabitsDatabase.getInstance(context).habitsDao()

    private var baseHabits: List<HabitInfo> = arrayListOf()
    private val processedHabits: MutableLiveData<List<HabitInfo>> = MutableLiveData()

    val habits: LiveData<List<HabitInfo>> = processedHabits

    init {
        model.getHabits().observe(lifecycleOwner) { habits ->
            baseHabits = habits
            selectByState()
        }
    }

    fun selectByName(name: String){
        state.searchPrefix = name
        selectByState()
    }

    fun selectByType(type: HabitType) {
        state.habitType = type
        selectByState()
    }

    fun orderByName(ordering: Ordering){
        state.ordering = ordering
        selectByState()
    }

    private fun selectByNamePrefixInternal(name: String) {
        if (name.isEmpty() || name.isBlank())
            return

        processedHabits.value =
            processedHabits.value?.filter { habit -> habit.name.startsWith(name, true) }
                ?.let { ArrayList(it) }
        Log.d(this::class.java.canonicalName, "Was selected by name $name")
    }

    private fun orderByNameInternal(ordering: Ordering) {
        processedHabits.value = when(ordering){
            Ordering.Ascending -> processedHabits.value?.sortedBy { it.name }?.let { ArrayList(it) }
            Ordering.Descending -> processedHabits.value?.sortedByDescending { it.name }?.let { ArrayList(it) }
        }
        Log.d(this::class.java.canonicalName, "Was ordered by $ordering")
    }

    private fun selectByTypeInternal(habitType: HabitType){
        processedHabits.value = ArrayList( baseHabits.filter { habit -> habit.type == habitType } )
    }

    private fun selectByState(){
        selectByTypeInternal(state.habitType)
        selectByNamePrefixInternal(state.searchPrefix)
        orderByNameInternal(state.ordering)
    }

    companion object {
        private var instance: HabitsListViewModel? = null

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            repository: IHabitsRepository,
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
                        instance = HabitsListViewModel(repository, lifecycleOwner)
                    return instance!! as T
                }
            }
    }
}