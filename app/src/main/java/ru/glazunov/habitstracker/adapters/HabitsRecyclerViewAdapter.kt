package ru.glazunov.habitstracker.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import ru.glazunov.habitstracker.models.LocalHabit
import ru.glazunov.habitstracker.R

class HabitsRecyclerViewAdapter(
    private val navController: NavController
) : RecyclerView.Adapter<HabitViewHolder>() {
    private var habitsInfos: List<LocalHabit> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_habit_info, parent, false)

        return HabitViewHolder(
            view,
            parent.context,
            navController
        )
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habitsInfos[position])
        holder.habit = habitsInfos[position]
    }

    override fun getItemCount() = habitsInfos.size

    @SuppressLint("NotifyDataSetChanged")
    fun setHabitInfos(habits: List<LocalHabit>) {
        habitsInfos = habits
        notifyDataSetChanged()
    }
}