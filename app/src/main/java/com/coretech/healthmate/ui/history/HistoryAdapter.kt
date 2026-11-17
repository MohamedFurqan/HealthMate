package com.coretech.healthmate.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coretech.healthmate.R
import com.coretech.healthmate.data.model.HealthRecord

class HistoryAdapter(
    private val records: List<HealthRecord>,
    private val onItemClick: (HealthRecord) -> Unit  // New: Callback for item click
) : RecyclerView.Adapter<HistoryAdapter.RecordViewHolder>() {

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvRecordDate)
        val tvSteps: TextView = view.findViewById(R.id.tvRecordSteps)
        val tvCalories: TextView = view.findViewById(R.id.tvRecordCalories)
        val tvWater: TextView = view.findViewById(R.id.tvRecordWater)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_health_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.tvDate.text = record.date
        holder.tvSteps.text = "Steps: ${record.steps}"
        holder.tvCalories.text = "Cal: ${record.calories}"
        holder.tvWater.text = "Water: ${record.water}ml"

        // Handle click
        holder.itemView.setOnClickListener {
            onItemClick(record)
        }
    }

    override fun getItemCount() = records.size
}