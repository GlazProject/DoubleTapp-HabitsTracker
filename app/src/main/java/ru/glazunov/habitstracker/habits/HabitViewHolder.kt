package ru.glazunov.habitstracker.habits

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_habit_info.view.*
import ru.glazunov.habitstracker.MainActivity
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.models.HabitInfo

class HabitViewHolder(
    private val view: View,
    private val context: Context,
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
        (context as MainActivity).editHabit(habitInfo, position)
    }
}