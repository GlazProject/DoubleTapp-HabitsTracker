package ru.glazunov.habitstracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HabitsAdapter(private val habitsInfos: MutableList<HabitInfo>) :
    RecyclerView.Adapter<HabitViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HabitViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.view_habit_info, parent, false)
        return HabitViewHolder(view, parent.context, null)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habitsInfos[position])
        holder.habitInfo = habitsInfos[position]
        holder.position = position
    }

    override fun getItemCount() = habitsInfos.size

    fun addHabitInfo(habitInfo: HabitInfo) {
        habitsInfos.add(habitInfo)
        notifyItemInserted(habitsInfos.size)
    }

    fun changeHabitInfo(habitInfoPosition: Int, habitInfo: HabitInfo) {
        habitsInfos[habitInfoPosition] = habitInfo
        notifyItemChanged(habitInfoPosition)
    }
}