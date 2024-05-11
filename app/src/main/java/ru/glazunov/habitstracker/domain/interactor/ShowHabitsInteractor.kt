package ru.glazunov.habitstracker.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitType
import ru.glazunov.habitstracker.domain.repositories.HabitsRepository
import ru.glazunov.habitstracker.domain.models.HabitsListState
import ru.glazunov.habitstracker.domain.models.Ordering

class ShowHabitsInteractor(
    private val repository: HabitsRepository
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

    suspend fun refresh() = repository.reload()

    suspend fun selectByName(name: String){
        state.searchPrefix = name
        selectByState()
    }

    suspend fun orderByName(ordering: Ordering){
        state.ordering = ordering
        selectByState()
    }

    private suspend fun selectByState(){
        repository.get(HabitType.POSITIVE, state.searchPrefix, state.ordering)
                .collect { positiveHabits.emit(it) }
        repository.get(HabitType.NEGATIVE, state.searchPrefix, state.ordering)
                .collect { negativeHabits.emit(it) }
    }
}