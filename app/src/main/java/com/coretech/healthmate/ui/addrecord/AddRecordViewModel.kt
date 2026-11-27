package com.coretech.healthmate.ui.addrecord

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.coretech.healthmate.data.model.HealthRecord
import com.coretech.healthmate.data.repository.HealthRepository
import com.coretech.healthmate.data.repository.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddRecordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HealthRepository(application)

    // LiveData to observe the result of the save operation
    private val _saveResult = MutableLiveData<OperationResult?>()
    val saveResult: LiveData<OperationResult?> get() = _saveResult

    // LiveData for validation errors
    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> get() = _validationError

    private var isSaving = false

    fun saveRecord(date: String, stepsStr: String, caloriesStr: String, waterStr: String) {
        if (isSaving) return // Prevent duplicate submissions

        // 1. Validation
        if (date.isBlank()) {
            _validationError.value = "Please select a date."
            return
        }

        val steps = validateNumber(stepsStr, "Steps", 100_000) ?: return
        val calories = validateNumber(caloriesStr, "Calories", 50_000) ?: return
        val water = validateNumber(waterStr, "Water", 20_000) ?: return

        // 2. Create the object
        val record = HealthRecord(date = date, steps = steps, calories = calories, water = water)

        // 3. Save via Repository on a Background Thread
        isSaving = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.insertRecord(record)
            
            // Switch back to Main thread to update LiveData
            launch(Dispatchers.Main) {
                _saveResult.value = result
                isSaving = false
            }
        }
    }

    private fun validateNumber(value: String, fieldName: String, maxValue: Int): Int? {
        if (value.isBlank()) {
            _validationError.value = "Please enter $fieldName."
            return null
        }
        val number = value.toLongOrNull()
        if (number == null) {
            _validationError.value = "$fieldName must be a valid whole number."
            return null
        }
        if (number < 0) {
            _validationError.value = "$fieldName cannot be negative."
            return null
        }
        if (number > maxValue) {
            _validationError.value = "$fieldName exceeds the realistic daily limit of $maxValue."
            return null
        }
        return number.toInt()
    }

    fun clearError() {
        _validationError.value = null
    }
}