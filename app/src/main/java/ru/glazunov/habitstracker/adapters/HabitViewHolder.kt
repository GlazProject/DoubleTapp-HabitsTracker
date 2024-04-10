package ru.glazunov.habitstracker.adapters

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_habit_info.view.*
import ru.glazunov.habitstracker.models.Constants
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.R

class HabitViewHolder(
    private val view: View,
    private val context: Context,
    private val navController: NavController,
    var position: Int?
) : RecyclerView.ViewHolder(view), View.OnClickListener {

    var habitInfo = HabitInfo()

    init {
        view.setOnClickListener(this)
    }

    fun bind(habitInfo: HabitInfo) {
        view.name.text = habitInfo.name
        view.description.text = habitInfo.description
        view.type.text = habitInfo.type
        view.priority.text = habitInfo.priority
        view.setBackgroundColor(habitInfo.color)
        view.period.text = context.getString(R.string.repeatInDays, habitInfo.repeatsCount, habitInfo.daysPeriod)
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putParcelable(Constants.FieldNames.HABIT_INFO, habitInfo)
        bundle.putInt(Constants.FieldNames.POSITION, position ?: -1)
        navController.navigate(R.id.action_mainFragment_to_habitEditingFragment, bundle)
    }
}