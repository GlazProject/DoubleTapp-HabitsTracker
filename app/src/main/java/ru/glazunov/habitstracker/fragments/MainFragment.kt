package ru.glazunov.habitstracker.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_main.*
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.adapters.HabitsViewPagerAdapter
import ru.glazunov.habitstracker.models.Ordering
import ru.glazunov.habitstracker.viewmodels.HabitsListViewModel

class MainFragment: Fragment() {
    private var editingFragment: HabitEditingFragment? = null
    private lateinit var habitsTypesList: ArrayList<String>
    private val viewModel: HabitsListViewModel by activityViewModels()


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
        fab.setOnClickListener(this::onFabClick)

        BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_EXPANDED
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.selectByName(s.toString())
                Log.d(this::class.java.canonicalName, "Write (from main) text $s")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        orderNameAscendingButton.setOnClickListener { viewModel.orderByName(Ordering.Ascending) }
        orderNameDescendingButton.setOnClickListener { viewModel.orderByName(Ordering.Descending) }

        val viewPager = mainPager
        viewPager.adapter = HabitsViewPagerAdapter(viewModel, this)
        val tabLayout = tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = habitsTypesList[position]
        }.attach()
    }

    private fun onFabClick(view: View) {
        editingFragment = HabitEditingFragment()
        requireActivity().findNavController(R.id.nav_host_fragment)
            .navigate(R.id.action_mainFragment_to_habitEditingFragment)
    }
}