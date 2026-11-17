package com.coretech.healthmate.ui.history

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coretech.healthmate.MainActivity
import com.coretech.healthmate.R
import com.coretech.healthmate.ui.editrecord.EditRecordFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvHistory)
        val tvEmptyState = view.findViewById<TextView>(R.id.tvEmptyState)
        val etSearchDate = view.findViewById<TextInputEditText>(R.id.etSearchDate)
        val btnClearFilter = view.findViewById<MaterialButton>(R.id.btnClearFilter)

        // 1. Setup RecyclerView
        adapter = HistoryAdapter(emptyList()) { clickedRecord ->
            val editFragment = EditRecordFragment().apply {
                arguments = Bundle().apply {
                    putInt("RECORD_ID", clickedRecord.id)
                }
            }
            (activity as? MainActivity)?.loadFragment(editFragment)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // 2. Observe Records
        viewModel.records.observe(viewLifecycleOwner) { records ->
            adapter.updateRecords(records)
            
            // Show/hide RecyclerView based on whether we have records
            if (records.isEmpty()) {
                recyclerView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.VISIBLE
            }
        }

        // 3. Observe Empty State Messages
        viewModel.emptyStateMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                tvEmptyState.text = message
                tvEmptyState.visibility = View.VISIBLE
            } else {
                tvEmptyState.visibility = View.GONE
            }
        }

        // 4. Setup Date Picker for Search
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        etSearchDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val selectedDate = dateFormat.format(calendar.time)
                etSearchDate.setText(selectedDate)
                viewModel.searchByDate(selectedDate)
            }, year, month, day)
            datePicker.show()
        }

        // 5. Clear Filter Button
        btnClearFilter.setOnClickListener {
            etSearchDate.setText("")
            viewModel.clearSearch()
        }

        // 6. Initial Load
        viewModel.loadAllRecords()
    }
}