package ru.glazunov.habitstracker.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import ru.glazunov.habitstracker.R
import ru.glazunov.habitstracker.models.Ordering
import ru.glazunov.habitstracker.repository.HabitsDatabase
import ru.glazunov.habitstracker.viewmodels.HabitsListViewModel

class BottomSheetFragment : BottomSheetDialogFragment() {
//    private val viewModel: HabitsListViewModel by activityViewModels()
    private val viewModel: HabitsListViewModel by viewModels {
    HabitsListViewModel.provideFactory(
        HabitsDatabase.getInstance(requireContext()).habitsDao(),
        requireActivity(),
        this
    )
}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(this::class.java.canonicalName, "bottom sheet created")
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(this::class.java.canonicalName, "Start bottom sheet registration")

        BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_EXPANDED
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.selectByName(s.toString())
                Log.d(this::class.java.canonicalName, "Write text $s")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        orderNameAscendingButton.setOnClickListener { viewModel.orderByName(Ordering.Ascending) }
        orderNameDescendingButton.setOnClickListener { viewModel.orderByName(Ordering.Descending) }
    }
}