package ru.glazunov.habitstracker.presentatin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import ru.glazunov.habitstracker.domain.interactor.ShowHabitsInteractor
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitType
import ru.glazunov.habitstracker.domain.models.Ordering
import javax.inject.Inject

@HiltViewModel
class HabitsListViewModel @Inject constructor(private val interactor: ShowHabitsInteractor)
    : ViewModel() {
    init {
        viewModelScope.launch(IO) {interactor.init()}
    }

    fun habits(type: HabitType): LiveData<List<Habit>> = interactor.getHabits(type).asLiveData()

    fun selectByName(name: String) = viewModelScope.launch(IO) {interactor.selectByName(name)}

    fun orderByName(ordering: Ordering) = viewModelScope.launch(IO) {interactor.orderByName(ordering)}
}