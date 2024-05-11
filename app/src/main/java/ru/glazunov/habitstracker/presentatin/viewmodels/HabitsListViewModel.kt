package ru.glazunov.habitstracker.presentatin.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import ru.glazunov.habitstracker.domain.interactor.ShowHabitsInteractor
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitType
import ru.glazunov.habitstracker.domain.models.Ordering

class HabitsListViewModel(
    private val interactor: ShowHabitsInteractor
): ViewModel() {
    init {
        viewModelScope.launch(IO) {interactor.init()}
    }

    fun habits(type: HabitType): LiveData<List<Habit>> = interactor.getHabits(type).asLiveData()

    fun selectByName(name: String) = viewModelScope.launch(IO) {interactor.selectByName(name)}

    fun orderByName(ordering: Ordering) = viewModelScope.launch(IO) {interactor.orderByName(ordering)}

    companion object {
        private var instance: HabitsListViewModel? = null

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            interactor: ShowHabitsInteractor,
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
                        instance = HabitsListViewModel(interactor)
                    return instance!! as T
                }
            }
    }
}