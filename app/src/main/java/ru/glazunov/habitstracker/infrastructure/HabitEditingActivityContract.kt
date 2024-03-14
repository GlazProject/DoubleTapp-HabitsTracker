package ru.glazunov.habitstracker.infrastructure

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.glazunov.habitstracker.habits.HabitEditingActivity
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.models.HabitInfoTransportWrapper

class HabitEditingActivityContract :
    ActivityResultContract<HabitInfoTransportWrapper, HabitInfoTransportWrapper>() {

    override fun createIntent(context: Context, input: HabitInfoTransportWrapper): Intent =
        Intent(context, HabitEditingActivity::class.java)
            .putExtra(Constants.HABIT_INFO, input.habitInfo)
            .putExtra(Constants.HABIT_INFO_POSITION, input.habitPosition)

    override fun parseResult(resultCode: Int, intent: Intent?): HabitInfoTransportWrapper = when {
        resultCode != Activity.RESULT_OK -> HabitInfoTransportWrapper()
        else -> {
            HabitInfoTransportWrapper(
                habitInfo = intent?.extras?.getParcelable(Constants.HABIT_INFO, HabitInfo::class.java),
                habitPosition = intent?.extras?.getInt(Constants.HABIT_INFO_POSITION, -1) ?: -1
            )
        }
    }
}