package com.coretech.healthmate.ui.editrecord

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.coretech.healthmate.MainActivity
import com.coretech.healthmate.R
import com.coretech.healthmate.data.repository.OperationResult
import com.coretech.healthmate.ui.history.HistoryFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditRecordFragment : Fragment(R.layout.fragment_edit_record) {

    private val viewModel: EditRecordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etDate = view.findViewById<TextInputEditText>(R.id.etEditDate)
        val etSteps = view.findViewById<TextInputEditText>(R.id.etEditSteps)
        val etCalories = view.findViewById<TextInputEditText>(R.id.etEditCalories)
        val etWater = view.findViewById<TextInputEditText>(R.id.etEditWater)
        val btnUpdate = view.findViewById<MaterialButton>(R.id.btnUpdate)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDelete)

        // Get the record ID passed from History
        val recordId = arguments?.getInt("RECORD_ID", -1) ?: -1
        if (recordId == -1) {
            Toast.makeText(requireContext(), "Invalid record.", Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.loadFragment(HistoryFragment())
            return
        }

        // Load the record from the database
        viewModel.loadRecord(recordId)

        // Observe the loaded record and populate the form
        viewModel.loadedRecord.observe(viewLifecycleOwner) { record ->
            if (record != null) {
                etDate.setText(record.date)
                etSteps.setText(record.steps.toString())
                etCalories.setText(record.calories.toString())
                etWater.setText(record.water.toString())
            }
        }

        // Observe record-not-found state
        viewModel.recordNotFound.observe(viewLifecycleOwner) { notFound ->
            if (notFound) {
                Toast.makeText(requireContext(), "This record no longer exists.", Toast.LENGTH_LONG).show()
                (activity as? MainActivity)?.loadFragment(HistoryFragment())
            }
        }

        // Observe validation errors
        viewModel.validationError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        // Observe operation results (Update/Delete)
        viewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is OperationResult.Success -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    (activity as? MainActivity)?.loadFragment(HistoryFragment())
                }
                is OperationResult.DuplicateDate -> {
                    Toast.makeText(requireContext(), "This date already has a record. Choose another.", Toast.LENGTH_LONG).show()
                }
                is OperationResult.NotFound -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    (activity as? MainActivity)?.loadFragment(HistoryFragment())
                }
                is OperationResult.Error -> {
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        // Date Picker for editing
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            try {
                calendar.time = dateFormat.parse(etDate.text.toString()) ?: calendar.time
            } catch (e: Exception) {
                // Fall back to today if parse fails
            }

            val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
                calendar.set(year, month, day)
                etDate.setText(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        // Update button
        btnUpdate.setOnClickListener {
            viewModel.updateRecord(
                date = etDate.text.toString(),
                stepsStr = etSteps.text.toString(),
                caloriesStr = etCalories.text.toString(),
                waterStr = etWater.text.toString()
            )
        }

        // Delete button with confirmation dialog
        btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete this health record?")
                .setMessage("This action will permanently remove this record. This cannot be undone.")
                .setPositiveButton("Delete") { dialog, _ ->
                    viewModel.deleteRecord()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}