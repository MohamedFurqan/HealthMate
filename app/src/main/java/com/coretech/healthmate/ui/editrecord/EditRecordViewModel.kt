package com.coretech.healthmate.ui.editrecord

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
import kotlinx.coroutines.withContext

class EditRecordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HealthRepository(application)

    private val _loadedRecord = MutableLiveData<HealthRecord?>()
    val loadedRecord: LiveData<HealthRecord?> get() = _loadedRecord

    private val _operationResult = MutableLiveData<OperationResult?>()
    val operationResult: LiveData<OperationResult?> get() = _operationResult

    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> get() = _validationError

    private var isSaving = false

    fun loadRecord(id: Int) {
        viewModelScope.launch {
            val record = withContext(Dispatchers.IO) {
                repository.getRecordById(id)
            }
            _loadedRecord.value = record
        }
    }

    fun updateRecord(date: String, stepsStr: String, caloriesStr: String, waterStr: String) {
        if (isSaving) return
        val currentRecord = _loadedRecord.value ?: return

        // Validation
        if (date.isBlank()) {
            _validationError.value = "Please select a date."
            return
        }
        val steps = validateNumber(stepsStr, "Steps") ?: return
        val calories = validateNumber(caloriesStr, "Calories") ?: return
        val water = validateNumber(waterStr, "Water") ?: return

        // Create updated record using .copy() to preserve the ID
        val updatedRecord = currentRecord.copy(
            date = date,
            steps = steps,
            calories = calories,
            water = water
        )

        isSaving = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateRecord(updatedRecord)
            launch(Dispatchers.Main) {
                _operationResult.value = result
                isSaving = false
            }
        }
    }

    fun deleteRecord() {
        if (isSaving) return
        val currentRecord = _loadedRecord.value ?: return

        isSaving = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteRecord(currentRecord.id)
            launch(Dispatchers.Main) {
                _operationResult.value = result
                isSaving = false
            }
        }
    }

    private fun validateNumber(value: String, fieldName: String): Int? {
        if (value.isBlank()) {
            _validationError.value = "Please enter $fieldName."
            return null
        }
        val number = value.toIntOrNull()
        if (number == null) {
            _validationError.value = "$fieldName must be a valid whole number."
            return null
        }
        if (number < 0) {
            _validationError.value = "$fieldName cannot be negative."
            return null
        }
        return number
    }

    fun clearError() {
        _validationError.value = null
    }
}