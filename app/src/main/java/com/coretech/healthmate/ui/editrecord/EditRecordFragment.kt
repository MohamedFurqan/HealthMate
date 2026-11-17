package com.coretech.healthmate.ui.editrecord

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.coretech.healthmate.R
import com.google.android.material.textfield.TextInputEditText

class EditRecordFragment : Fragment(R.layout.fragment_edit_record) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Populate with temporary sample data (to be replaced in Module 07)
        view.findViewById<TextInputEditText>(R.id.etEditDate).setText("2026-08-18")
        view.findViewById<TextInputEditText>(R.id.etEditSteps).setText("6500")
        view.findViewById<TextInputEditText>(R.id.etEditCalories).setText("320")
        view.findViewById<TextInputEditText>(R.id.etEditWater).setText("1500")

        // 2. Handle Buttons (Placeholder actions for Module 04)
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnUpdate).setOnClickListener {
            Toast.makeText(requireContext(), "Update will be implemented in Module 07", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDelete).setOnClickListener {
            Toast.makeText(requireContext(), "Delete will be implemented in Module 07", Toast.LENGTH_SHORT).show()
        }
    }
}