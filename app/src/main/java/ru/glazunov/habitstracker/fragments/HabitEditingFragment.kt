package ru.glazunov.habitstracker.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_habit_editing.*
import ru.glazunov.habitstracker.*
import ru.glazunov.habitstracker.models.Constants
import ru.glazunov.habitstracker.models.HabitInfo
import ru.glazunov.habitstracker.models.HabitType
import ru.glazunov.habitstracker.viewmodels.HabitEditingViewModel
import kotlin.math.round

class HabitEditingFragment : Fragment() {
    companion object {
        fun newInstance(
            position: Int? = null,
            habitInfo: HabitInfo = HabitInfo()
        ): HabitEditingFragment {
            val fragment = HabitEditingFragment()

            val bundle = Bundle()
            bundle.putParcelable(Constants.FieldNames.HABIT_INFO, habitInfo)

            if (position != null)
                bundle.putInt(Constants.FieldNames.POSITION, position)

            fragment.arguments = bundle
            return fragment
        }
    }

    private val colorPickerSquaresNumber = 16
    private var habitInfo = HabitInfo()
    private val habitEditingViewModel: HabitEditingViewModel by activityViewModels()

    private var habitChangedCallback: IHabitChangedCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        habitChangedCallback = activity as IHabitChangedCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_habit_editing, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null)
            restoreState(savedInstanceState)
        else
            arguments?.let { restoreState(it) }

        setListeners()
        chosenColorDisplay.setBackgroundColor(habitInfo.color)
        createColorButtons()
        colorPickerLayout.doOnLayout(this::onButtonsLayout)
        updateViews(habitInfo)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveUserInput()
        outState.putParcelable(Constants.FieldNames.HABIT_INFO, habitInfo)
    }

    override fun onStart() {
        super.onStart()
        updateViews(habitInfo)
    }

    private fun restoreState(bundle: Bundle) {
        habitInfo = bundle.getParcelable(Constants.FieldNames.HABIT_INFO) ?: HabitInfo()
    }

    private fun createColorButtons() {
        val colors = getGradientColors(10F, 0.85F, 0.95F)
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
        colorPickerLayout.background = gradientDrawable

        for (buttonNumber in 0 until colorPickerSquaresNumber) {
            val button = Button(context)
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
        chosenColorValue.text = color.toString()
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val rgb = hexToRgb(color)
        rgbColorValue.text = this.resources.getString(R.string.rgb_color, rgb[0], rgb[1], rgb[2])
        hsvColorValue.text = this.resources.getString(R.string.hsv_color, hsv[0], hsv[1], hsv[2])
    }

    private fun getGradientColors(hueStep: Float, saturation: Float, value: Float): IntArray {
        var currentHue = 0.0F
        val result = ArrayList<Int>()
        while (currentHue < 360) {
            result.add(Color.HSVToColor(floatArrayOf(currentHue, saturation, value)))
            currentHue += hueStep
        }
        return result.toIntArray()
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

    private fun hexToRgb(hex: Int): IntArray {
        val r = hex and 0xFF0000 shr 16
        val g = hex and 0xFF00 shr 8
        val b = hex and 0xFF
        return intArrayOf(r, g, b)
    }

    private fun setListeners() {
        saveButton.setOnClickListener(this::onSaveClick)
        cancelButton.setOnClickListener(this::onCancelClick)
    }


    private fun updateViews(habitInfo: HabitInfo?) {
        habitName.setText(habitInfo?.name ?: "")
        habitDescription.setText(habitInfo?.description ?: "")
        habitType.check(getCheckedButtonId(habitType, getHabitTypeString(habitInfo?.type)))
        habitPriority.setSelection(getSelectedItemPosition(habitPriority, habitInfo?.priority))
        habitRepeatDays.setText(habitInfo?.daysPeriod.toString())
        habitRepeatsCount.setText(habitInfo?.repeatsCount.toString())
    }

    private fun getSelectedItemPosition(spinner: Spinner, text: String?): Int {
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
        return radioGroup.children.first().id
    }

    private fun saveUserInput() {
        if (habitName == null)
            return

        habitInfo = HabitInfo(
            name = habitName.text.toString(),
            description = habitDescription.text.toString(),
            type = getHabitType(),
            repeatsCount = habitRepeatsCount.text.toString().toIntOrNull() ?: 0,
            daysPeriod = habitRepeatDays.text.toString().toIntOrNull() ?: 0,
            priority = habitPriority.selectedItem.toString(),
            color =  chosenColorValue.text.toString().toIntOrNull() ?: Color.WHITE
        )
    }

    private fun getHabitType(): HabitType =
        when(habitType.findViewById<RadioButton>(habitType.checkedRadioButtonId).text.toString()){
            getString(R.string.positive_habit) -> HabitType.POSITIVE
            getString(R.string.negative_habit) -> HabitType.NEGATIVE
            else -> throw IndexOutOfBoundsException()
        }

    private fun getHabitTypeString(type: HabitType?): String{
        return when (type){
            HabitType.POSITIVE -> getString(R.string.positive_habit)
            HabitType.NEGATIVE-> getString(R.string.negative_habit)
            else -> getString(R.string.positive_habit)
        }
    }

    private fun onSaveClick(view: View) {
        saveUserInput()
        habitEditingViewModel.changeHabit(habitInfo)
        (habitChangedCallback as IHabitChangedCallback).onHabitChanged() // возможно не нужно
    }

    private fun onCancelClick(view: View) {
        (habitChangedCallback as IHabitChangedCallback).onHabitChanged()
    }
}