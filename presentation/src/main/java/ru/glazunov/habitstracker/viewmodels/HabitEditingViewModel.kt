package ru.glazunov.habitstracker.viewmodels

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import ru.glazunov.domain.interactor.EditHabitInteractor
import ru.glazunov.domain.models.Habit
import ru.glazunov.domain.models.HabitPriority
import ru.glazunov.domain.models.HabitType
import ru.glazunov.domain.usecases.DeleteHabitUseCase
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HabitEditingViewModel @Inject constructor(
    private val interactor: EditHabitInteractor,
    private val deleteUseCase: DeleteHabitUseCase
)
    : ViewModel() {

    init{
        interactor.init()
    }

    fun getHabit(id: UUID): LiveData<Habit> {
        val data = MutableLiveData<Habit>()
        viewModelScope.launch {
            data.postValue(interactor.get(id))
        }
        return data
    }

    fun getId(): UUID = interactor.getId()

    fun saveHabit() = viewModelScope.launch(IO) {
        interactor.save()
    }

    fun deleteHabit()= viewModelScope.launch(IO) {
        deleteUseCase.delete(interactor.getId())
    }

    fun cancel() = interactor.cancel()

    fun validateTitle() = interactor.validateTitle()
    fun validateDescription() = interactor.validateDescription()

    fun setTitle(title: Editable?) = interactor.setTitle(title.toString())
    fun setDescription(description: Editable?) = interactor.setDescription(description.toString())
    fun setPriority(priority: Int) = interactor.setPriority(HabitPriority.fromInt(priority))
    fun setType(type: HabitType) = interactor.setType(type)
    fun setCount(count: Editable?) = interactor.setCount(count?.toString()?.toIntOrNull() ?: 0)
    fun setFrequency(frequency: Editable?) = interactor.setFrequency(frequency?.toString()?.toIntOrNull() ?: 0)
    fun setColor(color: Int) = interactor.setColor(color)
}