package ru.glazunov.habitstracker.adapters

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_habit_info.view.*
import ru.glazunov.habitstracker.models.Constants
import ru.glazunov.habitstracker.models.LocalHabit
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.models.HabitPriority
import ru.glazunov.habitstracker.models.HabitType

class HabitViewHolder(
    private val view: View,
    private val context: Context,
    private val navController: NavController,
) : RecyclerView.ViewHolder(view), View.OnClickListener {
    var habit = LocalHabit()

    init {
        view.setOnClickListener(this)
    }

    fun bind(habit: LocalHabit) {
        view.name.text = habit.name
        view.description.text = habit.description
        view.type.text = getHabitTypeString(habit.type)
        view.priority.text = getHabitPriorityString(habit.priority)
        view.setBackgroundColor(habit.color)
        view.period.text = context.getString(R.string.repeatInDays, habit.repeatsCount, habit.daysPeriod)
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putString(Constants.FieldNames.ID, habit.id.toString())
        navController.navigate(R.id.action_mainFragment_to_habitEditingFragment, bundle)
    }

    private fun getHabitTypeString(type: HabitType?): String{
        return when (type){
            HabitType.POSITIVE -> context.getString(R.string.positive_habit)
            HabitType.NEGATIVE-> context.getString(R.string.negative_habit)
            else -> context.getString(R.string.positive_habit)
        }
    }

    private fun getHabitPriorityString(priority: HabitPriority?): String =
        context.resources.getStringArray(R.array.habit_priorities)[priority?.value ?: 0]
}