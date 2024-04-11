package ru.glazunov.habitstracker.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.glazunov.habitstracker.adapters.HabitsViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_main.*
import ru.glazunov.habitstracker.models.Constants
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.models.Ordering
import ru.glazunov.habitstracker.viewmodels.HabitsListViewModel

class MainFragment: Fragment() {
    private val viewModel: HabitsListViewModel by activityViewModels()
    private var editingFragment: HabitEditingFragment? = null
    private lateinit var viewAdapter: HabitsViewPagerAdapter
    private lateinit var habitsTypesList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        habitsTypesList = arrayListOf(getString(R.string.positive_habits), getString(R.string.negative_habits))
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureBottomSheet()
        fab.setOnClickListener(this::onFabClick)
        configureAdapter()
    }

    private fun configureAdapter(){
        viewModel.habits.observe(viewLifecycleOwner, Observer { habit ->
            viewAdapter = HabitsViewPagerAdapter(
                ArrayList(habit.filter { habitInfo -> habitInfo.type == HabitType.POSITIVE }),
                ArrayList(habit.filter { habitInfo -> habitInfo.type == HabitType.NEGATIVE }),
                this
            )
            val viewPager = mainPager
            viewPager.adapter = viewAdapter
            val tabLayout = tabs
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = habitsTypesList[position]
            }.attach()
        })
    }

    private fun configureBottomSheet(){
        val bottomSheet = bottom_sheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.selectByName(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        orderNameAscendingButton.setOnClickListener { viewModel.orderByName(Ordering.Ascending) }
        orderNameDescendingButton.setOnClickListener { viewModel.orderByName(Ordering.Descending) }
    }

    private fun onFabClick(view: View) {
        editingFragment = HabitEditingFragment.newInstance()
        requireActivity().findNavController(R.id.nav_host_fragment)
            .navigate(R.id.action_mainFragment_to_habitEditingFragment)
    }
}