package ru.glazunov.habitstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_habit_list.*
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.adapters.HabitsRecyclerViewAdapter
import ru.glazunov.domain.models.HabitType
import ru.glazunov.habitstracker.viewmodels.HabitsListViewModel

class HabitListFragment(
    private val viewModel: HabitsListViewModel,
    private val type: HabitType
    ) : Fragment() { // лучше прописать ещё и пустой конструктор для автоматического создания при изменнеии стейта
        // создавать через провайдер и аргументы
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habit_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewAdapter = HabitsRecyclerViewAdapter(
            viewModel,
            requireActivity().findNavController(R.id.nav_host_fragment)
        )

        habitsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }

        viewModel.habits(type).observe(viewLifecycleOwner) { habits ->
            viewAdapter.setHabitInfos(habits)
        }
    }
}