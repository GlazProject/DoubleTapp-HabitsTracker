package ru.glazunov.habitstracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.glazunov.habitstracker.data.HabitsRepository
import ru.glazunov.habitstracker.models.Constants


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        refreshLayout.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO) {
                HabitsRepository.getInstance(applicationContext, this@MainActivity).syncHabits()
            }
            refreshLayout.isRefreshing = false
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
}
