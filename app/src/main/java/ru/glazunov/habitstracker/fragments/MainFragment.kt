package ru.glazunov.habitstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ru.glazunov.habitstracker.adapters.HabitsViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_main.*
import ru.glazunov.habitstracker.Constants
import ru.glazunov.habitstracker.HabitInfo
import ru.glazunov.habitstracker.R

class MainFragment : Fragment() {
    companion object {
        fun newInstance(
            positiveHabitInfos: ArrayList<HabitInfo>,
            negativeHabitInfos: ArrayList<HabitInfo>
        ): MainFragment {
            val fragment = MainFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(Constants.FieldNames.POSITIVE_HABIT_INFOS, positiveHabitInfos)
            bundle.putParcelableArrayList(Constants.FieldNames.NEGATIVE_HABIT_INFOS, negativeHabitInfos)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var viewAdapter: HabitsViewPagerAdapter
    private var positiveHabitInfos: ArrayList<HabitInfo> = arrayListOf()
    private var negativeHabitInfos: ArrayList<HabitInfo> = arrayListOf()
    private var editingFragment: HabitEditingFragment? = null
    private val habitsTypesList = arrayListOf(getString(R.string.positive_habits), getString(R.string.negative_habits))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener(this::onFabClick)
        arguments?.let {
            positiveHabitInfos =
                it.getParcelableArrayList(Constants.FieldNames.POSITIVE_HABIT_INFOS) ?: arrayListOf()
            negativeHabitInfos =
                it.getParcelableArrayList(Constants.FieldNames.NEGATIVE_HABIT_INFOS) ?: arrayListOf()
        }
        viewAdapter = HabitsViewPagerAdapter(
            positiveHabitInfos,
            negativeHabitInfos,
            this
        )
        val viewPager = mainPager
        viewPager.adapter = viewAdapter
        val tabLayout = tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = habitsTypesList[position]
        }.attach()
    }

    private fun onFabClick(view: View) {
        editingFragment = HabitEditingFragment.newInstance()
        activity!!.findNavController(R.id.nav_host_fragment)
            .navigate(R.id.action_mainFragment_to_habitEditingFragment)

    }
}