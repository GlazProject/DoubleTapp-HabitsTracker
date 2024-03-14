package ru.glazunov.habitstracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import ru.glazunov.habitstracker.habits.HabitsAdapter
import ru.glazunov.habitstracker.infrastructure.Constants
import ru.glazunov.habitstracker.infrastructure.HabitEditingActivityContract
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.models.HabitInfoTransportWrapper

class MainActivity : AppCompatActivity() {
    private val habitEditing = registerForActivityResult(HabitEditingActivityContract()) { result: HabitInfoTransportWrapper ->
        if (result.habitPosition != -1)
            habitViewAdapter.changeHabitInfo(result.habitPosition, result.habitInfo!!)
        else
            habitViewAdapter.addHabitInfo(result.habitInfo!!)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var habitViewAdapter: HabitsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var habitsInfos: ArrayList<HabitInfo> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener(this::onFabClick)
        if (savedInstanceState != null)
            habitsInfos = savedInstanceState
                .getParcelableArrayList(Constants.HABITS_INFOS, HabitInfo::class.java) as ArrayList<HabitInfo>

        viewManager = LinearLayoutManager(this)
        habitViewAdapter = HabitsAdapter(habitsInfos)
        recyclerView = habitsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = habitViewAdapter
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(Constants.HABITS_INFOS, habitsInfos)
    }

    private fun onFabClick(view: View) = editHabit()

    public fun editHabit(habitInfo: HabitInfo? = null, habitPosition: Int? = null){
        habitEditing.launch(HabitInfoTransportWrapper(habitInfo, habitPosition ?: -1))
    }
}