package ru.glazunov.habitstracker.domain.interactor

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitType
import ru.glazunov.habitstracker.domain.repositories.HabitsRepository
import ru.glazunov.habitstracker.domain.models.HabitsListState
import ru.glazunov.habitstracker.domain.models.Ordering
import ru.glazunov.habitstracker.domain.syncronization.HabitsSynchronizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowHabitsInteractor @Inject constructor(
    private val repository: HabitsRepository,
    private val synchronizer: HabitsSynchronizer
) {
    private val state = HabitsListState()
    private val positiveHabits: MutableStateFlow<List<Habit>> = MutableStateFlow(listOf())
    private val negativeHabits: MutableStateFlow<List<Habit>> = MutableStateFlow(listOf())

    suspend fun init(){
        selectByState()
        refresh()
    }

    fun getHabits(type: HabitType): Flow<List<Habit>> {
        return when(type){
            HabitType.POSITIVE -> positiveHabits
            HabitType.NEGATIVE -> negativeHabits
        }
    }

    suspend fun refresh() = synchronizer.refresh()

    suspend fun selectByName(name: String){
        state.searchPrefix = name
        selectByState()
    }

    suspend fun orderByName(ordering: Ordering){
        state.ordering = ordering
        selectByState()
    }

    private suspend fun selectByState() {
        withContext(IO) {
            launch {
                repository.get(HabitType.POSITIVE, state.searchPrefix, state.ordering)
                    .collect { positiveHabits.emit(it) }
            }
            launch {
                repository.get(HabitType.NEGATIVE, state.searchPrefix, state.ordering)
                    .collect { negativeHabits.emit(it) }
            }
        }
    }
}