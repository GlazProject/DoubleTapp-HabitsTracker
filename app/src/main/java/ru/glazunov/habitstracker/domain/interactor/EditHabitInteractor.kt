package ru.glazunov.habitstracker.domain.interactor

import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitPriority
import ru.glazunov.habitstracker.domain.models.HabitType
import ru.glazunov.habitstracker.domain.repositories.HabitsRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditHabitInteractor @Inject constructor(
    private val repository: HabitsRepository
) {
    private var habit: Habit? = null

    fun init(){
        habit = Habit()
    }

    suspend fun get(id: UUID): Habit {
        val loadedHabit = repository.get(id)
        if (loadedHabit != null)
            habit = loadedHabit
        return habit!!
    }

    fun getId(): UUID = habit!!.id

    suspend fun save() {
        repository.put(habit!!)
        habit = null
    }

    fun cancel(){
        habit = null
    }

    fun validateTitle(): Boolean = !(habit?.title.isNullOrEmpty())
    fun validateDescription(): Boolean = !(habit?.description.isNullOrEmpty())

    fun setTitle(title: String) {
        habit!!.title = title
    }

    fun setDescription(description: String){
        habit!!.description = description
    }

    fun setPriority(priority: HabitPriority){
        habit!!.priority = priority
    }

    fun setType(type: HabitType){
        habit!!.type = type
    }

    fun setCount(count: Int){
        habit!!.count = count
    }

    fun setFrequency(frequency: Int){
        habit!!.frequency = frequency
    }

    fun setColor(color: Int){
        habit!!.color = color
    }
}