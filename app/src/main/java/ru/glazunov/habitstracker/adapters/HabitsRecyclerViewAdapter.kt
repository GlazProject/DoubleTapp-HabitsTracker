package ru.glazunov.habitstracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.R

class HabitsRecyclerViewAdapter(
    private val habitsInfos: ArrayList<HabitInfo>,
    private val navController: NavController
) : RecyclerView.Adapter<HabitViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_habit_info, parent, false)

        return HabitViewHolder(
            view,
            parent.context,
            navController,
            null
        )
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habitsInfos[position])
        holder.habitInfo = habitsInfos[position]
        holder.position = position
    }

    override fun getItemCount() = habitsInfos.size
}