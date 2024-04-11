package ru.glazunov.habitstracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_habit_info.*
import ru.glazunov.habitstracker.models.Constants
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.repository.IHabitsRepository
import ru.glazunov.habitstracker.repository.MemoryRepository
import ru.glazunov.habitstracker.viewmodels.HabitEditingViewModel
import ru.glazunov.habitstracker.viewmodels.HabitsListViewModel
import java.util.logging.Logger


class MainActivity : AppCompatActivity(), IHabitChangedCallback {
    private val habitRepository: IHabitsRepository = MemoryRepository()
    private lateinit var habitsListViewModel: HabitsListViewModel
    private lateinit var habitEditingViewModel: HabitEditingViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        habitsListViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HabitsListViewModel(habitRepository) as T
            }
        }).get(HabitsListViewModel::class.java)

        habitEditingViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HabitEditingViewModel(habitRepository, habitsListViewModel) as T
            }
        }).get(HabitEditingViewModel::class.java)

        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_info -> {
                    onAppInfoMenuClick()
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_habits -> {
                    onHabitsListMenuClick()
                    drawerLayout.closeDrawers()
                    true
                }

                else -> false
            }
        }

        if (savedInstanceState != null) {
            navController.restoreState(savedInstanceState.getBundle(Constants.FieldNames.NAV_CONTROLLER_STATE))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(Constants.FieldNames.NAV_CONTROLLER_STATE, navController.saveState())
    }

    private fun onHabitsListMenuClick() = navController.navigate(R.id.mainFragment)

    private fun onAppInfoMenuClick() = navController.navigate(R.id.appInfoFragment)

    override fun onHabitChanged() {
        navController.navigate(R.id.action_habitEditingFragment_to_mainFragment)
    }
}
