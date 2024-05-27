package ru.glazunov.habitstracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.glazunov.domain.interactor.ShowHabitsInteractor
import ru.glazunov.domain.models.DoneHabitResult
import ru.glazunov.domain.models.Habit
import ru.glazunov.domain.models.HabitType
import ru.glazunov.domain.models.Ordering
import ru.glazunov.domain.usecases.DeleteHabitUseCase
import ru.glazunov.domain.usecases.DoneHabitUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HabitsListViewModel @Inject constructor(
    private val interactor: ShowHabitsInteractor,
    private val deleteUseCase: DeleteHabitUseCase,
    private val doneUseCase: DoneHabitUseCase
)
    : ViewModel() {
    init {
        viewModelScope.launch(IO) {interactor.init()}
    }

    fun habits(type: HabitType): LiveData<List<Habit>> = interactor.getHabits(type).asLiveData()

    fun selectByName(name: String) = viewModelScope.launch(IO) {interactor.selectByName(name)}

    fun orderByName(ordering: Ordering) = viewModelScope.launch(IO) {interactor.orderByName(ordering)}

    fun doneHabit(id: UUID, toastCallback: (DoneHabitResult) -> Unit) =
        viewModelScope.launch(IO) {
            val result = doneUseCase.done(id)
            withContext(Dispatchers.Main) {toastCallback(result)}
        }

    fun deleteHabit(id: UUID) = viewModelScope.launch(IO) {deleteUseCase.delete(id)}
}