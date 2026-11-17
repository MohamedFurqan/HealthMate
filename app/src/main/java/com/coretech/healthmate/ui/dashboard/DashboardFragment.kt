package com.coretech.healthmate.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.coretech.healthmate.MainActivity
import com.coretech.healthmate.R
import com.coretech.healthmate.ui.addrecord.AddRecordFragment
import com.coretech.healthmate.ui.history.HistoryFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Display Today's Date dynamically
        val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        val todayDate = dateFormat.format(Date())
        view.findViewById<TextView>(R.id.tvTodayDate).text = "Today, $todayDate"

        // 2. Set Temporary Sample Data
        view.findViewById<TextView>(R.id.tvStepsValue).text = "6,500"
        view.findViewById<TextView>(R.id.tvCaloriesValue).text = "320"
        view.findViewById<TextView>(R.id.tvWaterValue).text = "1,500"

        // 3. Handle Quick Action Buttons (Phase 8 Navigation)
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnQuickAdd).setOnClickListener {
            (activity as? MainActivity)?.loadFragment(AddRecordFragment())
        }

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnQuickHistory).setOnClickListener {
            (activity as? MainActivity)?.loadFragment(HistoryFragment())
        }
    }
}