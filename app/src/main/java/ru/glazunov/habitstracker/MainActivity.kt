package ru.glazunov.habitstracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.nav_view
import kotlinx.android.synthetic.main.activity_main.refreshLayout
import kotlinx.android.synthetic.main.app_bar_main.toolbar
import kotlinx.android.synthetic.main.nav_header_main.profileImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.glazunov.habitstracker.data.habits.HabitsRepository
import ru.glazunov.habitstracker.data.profile.converters.CircleTransform
import ru.glazunov.habitstracker.models.Constants


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_habits, R.id.nav_info
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)

        refreshLayout.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO) {
                HabitsRepository.getInstance(applicationContext).syncHabits(lifecycleScope)
            }
            refreshLayout.isRefreshing = false
        }

        if (savedInstanceState != null) {
            navController.restoreState(savedInstanceState.getBundle(Constants.FieldNames.NAV_CONTROLLER_STATE))
        }

        loadProfileImage()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(Constants.FieldNames.NAV_CONTROLLER_STATE, navController.saveState())
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    private fun loadProfileImage(){
        lifecycleScope.launch(Dispatchers.Main) {
            Picasso.with(this@MainActivity)
                .load("https://gas-kvas.com/grafic/uploads/posts/2023-09/1695822868_gas-kvas-com-p-kartinki-milie-kotiki-13.jpg")
                .transform(CircleTransform())
                .into(profileImage)
        }
    }
}
