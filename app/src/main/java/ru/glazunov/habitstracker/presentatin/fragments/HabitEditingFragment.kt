package ru.glazunov.habitstracker.presentatin.fragments

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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_habit_editing.*
import ru.glazunov.habitstracker.*
import ru.glazunov.habitstracker.domain.models.Habit
import ru.glazunov.habitstracker.domain.models.HabitType
import ru.glazunov.habitstracker.domain.models.HabitPriority
import ru.glazunov.habitstracker.domain.repositories.HabitsRepository
import ru.glazunov.habitstracker.presentatin.viewmodels.HabitEditingViewModel
import java.util.UUID
import kotlin.math.round

class HabitEditingFragment : Fragment() {
    private val colorPickerSquaresNumber = 16
    private val viewModel: HabitEditingViewModel by viewModels {
        HabitEditingViewModel.provideFactory(
            HabitsRepository.getInstance(requireContext(), lifecycleScope),
            requireActivity()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_habit_editing, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { loadHabit(it) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null)
            loadHabit(savedInstanceState)

        setListeners()
        createColorButtons()
        colorPickerLayout.doOnLayout(this::onButtonsLayout)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("id", viewModel.getId().toString())
    }

    private fun loadHabit(bundle: Bundle) {
        val id = UUID.fromString(bundle.getString("id"))
        viewModel.getHabit(id).observe(viewLifecycleOwner){updateViews(it)}
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
        viewModel.setColor(color)
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val rgb = hexToRgb(color)
        rgbColorValue.text = this.resources.getString(R.string.rgb_color, rgb[0], rgb[1], rgb[2])
        hsvColorValue.text = this.resources.getString(R.string.hsv_color, hsv[0], hsv[1], hsv[2])
    }

    @Suppress("SameParameterValue")
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
        deleteButton.setOnClickListener(this::onDeleteClick)

        habitName.addTextChangedListener { text ->
            nameWarning.visibility = View.INVISIBLE
            viewModel.setTitle(text)
        }
        habitDescription.addTextChangedListener { text ->
            descriptionWarning.visibility = View.INVISIBLE
            viewModel.setDescription(text)
        }
        habitRepeatsCount.addTextChangedListener{ text -> viewModel.setCount(text) }
        habitRepeatDays.addTextChangedListener{ text -> viewModel.setFrequency(text) }

        habitType.setOnCheckedChangeListener { group, id ->
            viewModel.setType(when (group.findViewById<RadioButton>(id).text.toString()) {
                getString(R.string.positive_habit) -> HabitType.POSITIVE
                getString(R.string.negative_habit) -> HabitType.NEGATIVE
                else -> throw IndexOutOfBoundsException()
            })
        }

        habitPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?){}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setPriority(id.toInt())
            }
        }
    }

    private fun updateViews(habit: Habit?) {
        chosenColorDisplay.setBackgroundColor(habit?.color ?: Color.WHITE)
        habitName.setText(habit?.title ?: "")
        habitDescription.setText(habit?.description ?: "")
        habitType.check(getCheckedButtonId(habitType, getHabitTypeString(habit?.type)))
        habitPriority.setSelection(getSelectedItemPosition(habitPriority, getHabitPriorityString(habit?.priority)))
        habitRepeatDays.setText(habit?.frequency.toString())
        habitRepeatsCount.setText(habit?.count.toString())
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

    private fun getHabitTypeString(type: HabitType?): String{
        return when (type){
            HabitType.POSITIVE -> getString(R.string.positive_habit)
            HabitType.NEGATIVE-> getString(R.string.negative_habit)
            else -> getString(R.string.positive_habit)
        }
    }

    private fun getHabitPriorityString(priority: HabitPriority?): String =
        resources.getStringArray(R.array.habit_priorities)[priority?.value ?: 0]

    private fun validateName(): Boolean{
        if (viewModel.validateTitle())
            return true
        nameWarning.visibility = View.VISIBLE
        return false
    }

    private fun validateDescription(): Boolean{
        if (viewModel.validateDescription())
            return true
        descriptionWarning.visibility = View.VISIBLE
        return false
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSaveClick(view: View) {
        if (!validateName() || !validateDescription()) return

        viewModel.saveHabit()
        requireActivity().findNavController(R.id.nav_host_fragment)
            .navigate(R.id.action_habitEditingFragment_to_mainFragment)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onDeleteClick(view: View) {
        viewModel.deleteHabit()
        requireActivity().findNavController(R.id.nav_host_fragment)
            .navigate(R.id.action_habitEditingFragment_to_mainFragment)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onCancelClick(view: View) {
        viewModel.cancel()
        requireActivity().findNavController(R.id.nav_host_fragment)
            .navigate(R.id.action_habitEditingFragment_to_mainFragment)
    }
}