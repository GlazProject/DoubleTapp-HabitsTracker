package ru.glazunov.habitstracker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering
import ru.glazunov.habitstracker.repository.IHabitsRepository
import java.util.Locale.filter

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
        processedHabits.value =
            processedHabits.value?.filter { habit -> habit.name.startsWith(name, true) }
                ?.let { ArrayList(it) }
        Log.d(this::class.java.canonicalName, "Was selected by name $name")
    }

    fun selectPositive() {
        processedHabits.value = ArrayList( baseHabits.filter { habit -> habit.type == HabitType.POSITIVE } )
    }

    fun selectNegative() {
        processedHabits.value = ArrayList( baseHabits.filter { habit -> habit.type == HabitType.NEGATIVE } )
    }

    fun orderByName(ordering: Ordering) {
        processedHabits.value = when(ordering){
            Ordering.Ascending -> processedHabits.value?.sortedBy { it.name }?.let { ArrayList(it) }
            Ordering.Descending -> processedHabits.value?.sortedByDescending { it.name }?.let { ArrayList(it) }
        }
        Log.d(this::class.java.canonicalName, "Was ordered by $ordering")
    }

    fun notifyItemsChanged() {
        load()
    }
}