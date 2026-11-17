package com.coretech.healthmate.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.coretech.healthmate.MainActivity
import com.coretech.healthmate.R
import com.coretech.healthmate.ui.addrecord.AddRecordFragment
import com.coretech.healthmate.ui.history.HistoryFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTodayDate = view.findViewById<TextView>(R.id.tvTodayDate)
        val tvStepsValue = view.findViewById<TextView>(R.id.tvStepsValue)
        val tvCaloriesValue = view.findViewById<TextView>(R.id.tvCaloriesValue)
        val tvWaterValue = view.findViewById<TextView>(R.id.tvWaterValue)
        val tvNoRecordMessage = view.findViewById<TextView>(R.id.tvNoRecordMessage)

        // 1. Display Today's Date
        val displayFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        tvTodayDate.text = "Today, ${displayFormat.format(Date())}"

        // 2. Observe Today's Record from ViewModel
        viewModel.todayRecord.observe(viewLifecycleOwner) { record ->
            if (record != null) {
                // Record exists: display values
                tvStepsValue.text = String.format("%,d", record.steps)
                tvCaloriesValue.text = String.format("%,d", record.calories)
                tvWaterValue.text = String.format("%,d", record.water)
                tvNoRecordMessage.visibility = View.GONE
            } else {
                // No record: display zeros + message
                tvStepsValue.text = "0"
                tvCaloriesValue.text = "0"
                tvWaterValue.text = "0"
                tvNoRecordMessage.visibility = View.VISIBLE
            }
        }

        // 3. Quick Action Buttons
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnQuickAdd).setOnClickListener {
            (activity as? MainActivity)?.loadFragment(AddRecordFragment())
        }

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnQuickHistory).setOnClickListener {
            (activity as? MainActivity)?.loadFragment(HistoryFragment())
        }

        // 4. Load today's record every time the Fragment is created (refresh on navigation)
        viewModel.loadTodayRecord()
    }
}