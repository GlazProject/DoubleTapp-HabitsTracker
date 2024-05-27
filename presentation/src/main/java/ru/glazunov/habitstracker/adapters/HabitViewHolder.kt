package ru.glazunov.habitstracker.adapters

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_habit_info.view.*
import ru.glazunov.habitstracker.R
import ru.glazunov.domain.models.DoneHabitResult
import ru.glazunov.domain.models.DoneHabitResultType
import ru.glazunov.domain.models.Habit
import ru.glazunov.domain.models.HabitPriority
import ru.glazunov.domain.models.HabitType
import ru.glazunov.habitstracker.viewmodels.HabitsListViewModel

class HabitViewHolder(
    private val view: View,
    private val context: Context,
    private val viewModel: HabitsListViewModel,
    private val navController: NavController,
) : RecyclerView.ViewHolder(view), View.OnClickListener {
    var habit: Habit = Habit()

    init {
        view.setOnClickListener(this)
    }

    fun bind(habit: Habit) {
        view.name.text = habit.title
        view.description.text = habit.description
        view.type.text = getHabitTypeString(habit.type)
        view.priority.text = getHabitPriorityString(habit.priority)
        view.setBackgroundColor(habit.color)
        view.period.text = context.getString(R.string.repeatInDays,
            habit.count, timesString(habit.count),
            habit.frequency, context.resources.getQuantityString(R.plurals.days, habit.frequency))
        view.doneButton.setOnClickListener{ viewModel.doneHabit(habit.id, this::showDoneToast)}
        view.deleteHabitPreview.setOnClickListener{viewModel.deleteHabit(habit.id)}
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putString("id", habit.id.toString())
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

    private fun showDoneToast(result: DoneHabitResult){
        val text = when(result.type){
            DoneHabitResultType.Done -> context.getString(R.string.habitDoneSuccessful)
            DoneHabitResultType.NeedMore -> context.getString(R.string.requireHabitDoneCount, result.value, timesString(result.value))
            DoneHabitResultType.NotMoreThen -> context.getString(R.string.notMoreHabitDoneCount, result.value, timesString(result.value))+result.value
            DoneHabitResultType.RunOut -> context.getString(R.string.habitRunOut)
            DoneHabitResultType.Error -> context.getString(R.string.error)
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun timesString(times: Int?) = context.resources.getQuantityString(R.plurals.times, times ?: 0)
}