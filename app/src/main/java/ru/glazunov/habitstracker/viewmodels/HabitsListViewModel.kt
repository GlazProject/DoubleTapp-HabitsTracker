package ru.glazunov.habitstracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.models.Ordering
import ru.glazunov.habitstracker.repository.IHabitsRepository

class HabitsListViewModel(private val model: IHabitsRepository): ViewModel() {
    private lateinit var baseHabits: ArrayList<HabitInfo>
    private val processedHabits: MutableLiveData<ArrayList<HabitInfo>> = MutableLiveData()

    val habits: LiveData<ArrayList<HabitInfo>> = processedHabits

    init {
        load()
    }

    private fun load() {
        val habits = model.getHabits()
        processedHabits.value = habits
        baseHabits = habits
    }

    fun selectByName(name: String) {
        processedHabits.value = ArrayList( baseHabits.filter { habit -> habit.name.startsWith(name) } )
    }

    fun orderByName(ordering: Ordering) {
        processedHabits.value = when(ordering){
            Ordering.Ascending -> ArrayList(processedHabits.value!!.sortedBy { it.name })
            Ordering.Descending -> ArrayList(processedHabits.value!!.sortedByDescending { it.name })
        }
    }

    fun notifyItemsChanged() {
        load()
    }
}