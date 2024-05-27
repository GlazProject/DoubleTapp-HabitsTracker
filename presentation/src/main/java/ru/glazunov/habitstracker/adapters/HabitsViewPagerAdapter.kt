package ru.glazunov.habitstracker.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.glazunov.habitstracker.fragments.HabitListFragment
import ru.glazunov.domain.models.HabitType
import ru.glazunov.habitstracker.viewmodels.HabitsListViewModel


class HabitsViewPagerAdapter(private val viewModel: HabitsListViewModel, parent: Fragment): FragmentStateAdapter(parent) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment =
        if (position == 0) HabitListFragment(viewModel, HabitType.POSITIVE)
        else HabitListFragment(viewModel, HabitType.NEGATIVE)
}