package ru.glazunov.habitstracker.viewmodels

import androidx.lifecycle.ViewModel
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.models.Ordering
import ru.glazunov.habitstracker.repository.IHabitsRepository
import java.util.*

class HabitEditingViewModel(
    private val model: IHabitsRepository,
) : ViewModel() {

    fun getHabit(id: UUID) = model.getHabit(id)

    fun changeHabit(habitInfo: HabitInfo) {
        model.putHabit(habitInfo)
    }
}