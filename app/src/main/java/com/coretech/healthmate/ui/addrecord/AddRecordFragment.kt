package com.coretech.healthmate.ui.addrecord

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.coretech.healthmate.MainActivity
import com.coretech.healthmate.R
import com.coretech.healthmate.data.repository.OperationResult
import com.coretech.healthmate.ui.dashboard.DashboardFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddRecordFragment : Fragment(R.layout.fragment_add_record) {

    private val viewModel: AddRecordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etDate = view.findViewById<TextInputEditText>(R.id.etDate)
        val etSteps = view.findViewById<TextInputEditText>(R.id.etSteps)
        val etCalories = view.findViewById<TextInputEditText>(R.id.etCalories)
        val etWater = view.findViewById<TextInputEditText>(R.id.etWater)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        //Setup Date Picker (Default to today)
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        etDate.setText(dateFormat.format(calendar.time))

        etDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                etDate.setText(dateFormat.format(calendar.time))
            }, year, month, day)
            datePicker.show()
        }

        //Observe Validation Errors
        viewModel.validationError.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        // Observe Save Results
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is OperationResult.Success -> {
                    Toast.makeText(requireContext(), "Record saved successfully!", Toast.LENGTH_SHORT).show()
                    // Navigate back to Dashboard
                    (activity as? MainActivity)?.loadFragment(DashboardFragment())
                }
                is OperationResult.DuplicateDate -> {
                    Toast.makeText(requireContext(), "A record already exists for this date.", Toast.LENGTH_LONG).show()
                }
                is OperationResult.Error -> {
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        // Handle Save Button Click
        btnSave.setOnClickListener {
            viewModel.saveRecord(
                date = etDate.text.toString(),
                stepsStr = etSteps.text.toString(),
                caloriesStr = etCalories.text.toString(),
                waterStr = etWater.text.toString()
            )
        }
    }
}