package com.coretech.healthmate.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coretech.healthmate.MainActivity
import com.coretech.healthmate.R
import com.coretech.healthmate.data.model.HealthRecord
import com.coretech.healthmate.ui.editrecord.EditRecordFragment

class HistoryFragment : Fragment(R.layout.fragment_history) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Temporary Sample Data (To be replaced in Module 06)
        val sampleRecords = listOf(
            HealthRecord(id = 1, date = "2026-08-18", steps = 6500, calories = 320, water = 1500),
            HealthRecord(id = 2, date = "2026-08-17", steps = 8200, calories = 410, water = 2100),
            HealthRecord(id = 3, date = "2026-08-16", steps = 4300, calories = 200, water = 1800)
        )

        // 2. Setup RecyclerView with Click Listener
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = HistoryAdapter(sampleRecords) { clickedRecord ->
            // Navigate to Edit screen when a record is clicked
            (activity as? MainActivity)?.loadFragment(EditRecordFragment())
        }
    }
}