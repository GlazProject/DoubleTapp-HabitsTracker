package ru.glazunov.habitstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.glazunov.habitstracker.adapters.HabitsRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_habit_list.*
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.viewmodels.HabitsListViewModel

class HabitListFragment : Fragment() {
    private val viewModel: HabitsListViewModel by activityViewModels()
    private lateinit var viewAdapter: HabitsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_habit_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewManager = LinearLayoutManager(context)
        viewAdapter = HabitsRecyclerViewAdapter(
                viewModel.habits.value?: arrayListOf(),
                requireActivity().findNavController(R.id.nav_host_fragment)
            )

        habitsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}