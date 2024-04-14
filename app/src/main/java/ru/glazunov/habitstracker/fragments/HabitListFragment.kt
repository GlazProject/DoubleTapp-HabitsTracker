package ru.glazunov.habitstracker.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_habit_editing.*
import kotlinx.android.synthetic.main.fragment_habit_list.*
import ru.glazunov.habitstracker.IHabitChangedCallback
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.adapters.HabitsRecyclerViewAdapter
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.viewmodels.HabitsListViewModel

class HabitListFragment : Fragment() {
    private var habitChangedCallback: IHabitChangedCallback? = null
    private val viewModel: HabitsListViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        habitChangedCallback = activity as IHabitChangedCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habit_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewAdapter =
            HabitsRecyclerViewAdapter(
                viewModel.habits.value ?: listOf(),
                requireActivity().findNavController(R.id.nav_host_fragment)
            )
        habitsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }

        viewModel.habits.observe(viewLifecycleOwner, Observer { habits ->
            Log.d(this::class.java.canonicalName, "Update habits list")
            viewAdapter.setHabitInfos(habits)
        })
    }
}