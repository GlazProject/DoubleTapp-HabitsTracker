package ru.glazunov.habitstracker.presentatin.viewmodels

import android.os.Bundle
import android.text.Editable
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import ru.glazunov.habitstracker.domain.interactor.EditHabitInteractor
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitPriority
import ru.glazunov.habitstracker.domain.models.HabitType
import java.util.*

class HabitEditingViewModel(
    private val interactor: EditHabitInteractor
) : ViewModel() {
    fun getHabit(id: UUID): LiveData<Habit> {
        val data = MutableLiveData<Habit>()
        viewModelScope.launch {
            data.value = interactor.get(id)
        }
        return data
    }

    fun getId(): UUID = interactor.getId()

    fun saveHabit() = viewModelScope.launch(IO) {
        interactor.save()
    }

    fun deleteHabit()= viewModelScope.launch(IO) {
        interactor.delete()
    }

    fun cancel() = interactor.cancel()

    fun validateTitle() = interactor.validateTitle()
    fun validateDescription() = interactor.validateDescription()

    fun setTitle(title: Editable?) = interactor.setTitle(title.toString())
    fun setDescription(description: Editable?) = interactor.setDescription(description.toString())
    fun setPriority(priority: Int) = interactor.setPriority(HabitPriority.fromInt(priority))
    fun setType(type: HabitType) = interactor.setType(type)
    fun setCount(count: Editable?) = interactor.setCount(count.toString().toIntOrNull() ?: 0)
    fun setFrequency(frequency: Editable?) = interactor.setFrequency(frequency.toString().toIntOrNull() ?: 0)
    fun setColor(color: Int) = interactor.setColor(color)

    companion object {
        private var factory: AbstractSavedStateViewModelFactory? = null
        private var instance: HabitEditingViewModel? = null

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            interactor: EditHabitInteractor,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory {
            if (factory == null)
                factory = object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                    override fun <T : ViewModel> create(
                        key: String,
                        modelClass: Class<T>,
                        handle: SavedStateHandle
                    ): T {
                        if (instance == null)
                            instance = HabitEditingViewModel(interactor)
                        return instance!! as T
                    }
                }
            return factory!!
        }
    }
}