package ru.glazunov.habitstracker.presentatin.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.presentatin.viewmodels.HabitsListViewModel

class HabitsRecyclerViewAdapter(
    private val viewModel: HabitsListViewModel,
    private val navController: NavController
) : RecyclerView.Adapter<HabitViewHolder>() {
    private var habitsInfos: List<Habit> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_habit_info, parent, false)

        return HabitViewHolder(
            view,
            parent.context,
            viewModel,
            navController
        )
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habitsInfos[position])
        holder.habit = habitsInfos[position]
    }

    override fun getItemCount() = habitsInfos.size

    @SuppressLint("NotifyDataSetChanged")
    fun setHabitInfos(habits: List<Habit>) {
        habitsInfos = habits
        notifyDataSetChanged()
    }
}