package ru.glazunov.habitstracker.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_habits_main.fab
import kotlinx.android.synthetic.main.fragment_habits_main.mainPager
import kotlinx.android.synthetic.main.fragment_habits_main.tabs
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.adapters.HabitsViewPagerAdapter
import ru.glazunov.habitstracker.data.HabitsRepository
import ru.glazunov.habitstracker.models.Ordering
import ru.glazunov.habitstracker.viewmodels.HabitsListViewModel

class MainFragment: Fragment() {
    private lateinit var habitTypesList: ArrayList<String>

    private val viewModel: HabitsListViewModel by viewModels {
    HabitsListViewModel.provideFactory(
        HabitsRepository.getInstance(requireContext(), requireActivity()),
        requireActivity(),
        this
    )
}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        habitTypesList = arrayListOf(getString(R.string.positive_habits), getString(R.string.negative_habits))
        return inflater.inflate(R.layout.fragment_habits_main, container, false)
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

        mainPager.adapter = HabitsViewPagerAdapter(viewModel, this)
        TabLayoutMediator(tabs, mainPager) { tab, position ->
            tab.text = habitTypesList[position]
        }.attach()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onFabClick(view: View) {
        requireActivity().findNavController(R.id.nav_host_fragment)
            .navigate(R.id.action_mainFragment_to_habitEditingFragment)
    }
}