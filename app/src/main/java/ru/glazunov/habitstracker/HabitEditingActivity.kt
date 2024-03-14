package ru.glazunov.habitstracker

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.children
import androidx.core.view.doOnLayout
import kotlinx.android.synthetic.main.activity_habit_editing.*
import java.util.LinkedList
import kotlin.math.round


class HabitEditingActivity : AppCompatActivity() {
    companion object {
        private const val colorPickerSquaresNumber = 10

        private fun hsv2rgb(hex: Int): IntArray {
            val r = hex and 0xFF0000 shr 16
            val g = hex and 0xFF00 shr 8
            val b = hex and 0xFF
            return intArrayOf(r, g, b)
        }

        private fun getGradientColors(hueStep: Float, saturation: Float, value: Float): IntArray {
            var currentHue = 0.0F
            val result = LinkedList<Int>()
            while (currentHue < 360) {
                result.add(Color.HSVToColor(floatArrayOf(currentHue, saturation, value)))
                currentHue += hueStep
            }
            return result.toIntArray()
        }

        private fun getSelectedSpinnerPosition(spinner: Spinner, text: String?): Int {
            val elementsCount = spinner.count
            for (elementPos in 0 until elementsCount) {
                if (spinner.getItemAtPosition(elementPos).toString() == text)
                    return elementPos
            }
            return 0
        }

        private fun getCheckedButtonId(radioGroup: RadioGroup, text: String?): Int {
            for (child in radioGroup.children) {
                if ((child as RadioButton).text == text)
                    return child.id
            }
            return 0
        }
    }

    private var habitInfo: HabitInfo? = null
    private var habitInfoPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_editing)

        habitInfo = intent?.extras?.getParcelable(Constants.HABIT_INFO, HabitInfo::class.java)
        habitInfoPosition = intent.extras?.getInt(Constants.HABIT_INFO_POSITION, -1) ?: -1

        setListeners()
        setupColorLayout()

        if (habitInfo != null) {
            restoreHabitInfoValues(habitInfo)
        }
    }

    private fun createColorButtons() {
        val colors = getGradientColors(30F, 0.8F, 0.3F)
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
        colorPickerLayout.background = gradientDrawable
        for (buttonNumber in 0 until colorPickerSquaresNumber) {
            val button = Button(this)
            button.setBackgroundColor(colors[0])
            val params = LinearLayout.LayoutParams(200, 200)
            params.setMargins(50, 50, 50, 50)
            button.layoutParams = params
            button.setOnClickListener(this::onColorSquareClick)
            colorPickerLayout.addView(button)
        }
    }

    private fun onColorSquareClick(view: View) {
        val color = (view.background as ColorDrawable).color
        chosenColorDisplay.setBackgroundColor(color)
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val rgb = hsv2rgb(color)
        rgbColorValue.text = getString(R.string.rgb_color, rgb[0], rgb[1], rgb[2])
        hsvColorValue.text = getString(R.string.hsv_color, hsv[0], hsv[1], hsv[2])
    }

    private fun onButtonsLayout(view: View) {
        val layout = view as LinearLayout
        val drawable = layout.background as GradientDrawable
        val mutableBitmap = Bitmap.createBitmap(layout.width, layout.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        drawable.setBounds(0, 0, layout.width, layout.height)
        drawable.draw(canvas)
        for (btn in layout.children) {
            val pixelX = round(btn.x + btn.width / 2).toInt()
            val pixelY = round(btn.y + btn.height / 2).toInt()
            val pixel = mutableBitmap.getPixel(pixelX, pixelY)
            btn.setBackgroundColor(Color.rgb(pixel.red, pixel.green, pixel.blue))
        }
    }

    private fun setListeners() {
        saveButton.setOnClickListener {
            saveUserInput()
            setResult(Activity.RESULT_OK, getUpdatedIntent())
            finish()
        }
        cancelButton.setOnClickListener{
            setResult(Activity.RESULT_OK, getUpdatedIntent())
            finish()
        }
    }

    private fun setupColorLayout(){
        chosenColorDisplay.setBackgroundColor(habitInfo?.color ?: Color.WHITE)
        createColorButtons()
        colorPickerLayout.doOnLayout(this::onButtonsLayout)
    }

    private fun restoreHabitInfoValues(habitInfo: HabitInfo?) {
        habitName.setText(habitInfo?.name ?: "")
        habitDescription.setText(habitInfo?.description ?: "")
        habitType.check(getCheckedButtonId(habitType, habitInfo?.type))
        habitPriority.setSelection(getSelectedSpinnerPosition(habitPriority, habitInfo?.priority))
        habitRepeatsCount.setText(habitInfo?.repeatsCount.toString())
        habitRepeatDays.setText(habitInfo?.daysPeriod.toString())
    }

    private fun saveUserInput() {
        habitInfo = HabitInfo(
            name = habitName.text.toString(),
            description = habitDescription.text.toString(),
            type = findViewById<RadioButton>(habitType.checkedRadioButtonId).text.toString(),
            repeatsCount = habitRepeatsCount.text.toString().toIntOrNull() ?: 0,
            daysPeriod = habitRepeatDays.text.toString().toIntOrNull() ?: 0,
            priority = habitPriority.selectedItem.toString(),
            color = chosenColorDisplay.backgroundTintList?.defaultColor?: Color.WHITE
        )
    }

    private fun getUpdatedIntent(): Intent {
        val updatedIntent = Intent()
        if (habitInfo == null)
            return updatedIntent
        return updatedIntent
            .putExtra(Constants.HABIT_INFO, habitInfo)
            .putExtra(Constants.HABIT_INFO_POSITION, habitInfoPosition)
    }
}
