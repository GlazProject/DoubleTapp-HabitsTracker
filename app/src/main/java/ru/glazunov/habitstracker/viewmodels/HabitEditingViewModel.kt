package ru.glazunov.habitstracker.viewmodels

import androidx.lifecycle.ViewModel
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.models.Ordering
import ru.glazunov.habitstracker.repository.IHabitsRepository

class HabitEditingViewModel(
    private val model: IHabitsRepository,
    private val habitsListViewModel: HabitsListViewModel
) : ViewModel() {

    fun changeHabit(habitInfo: HabitInfo) {
        model.putHabit(habitInfo)
        habitsListViewModel.notifyItemsChanged()
        habitsListViewModel.orderByName(Ordering.Descending)
    }
}