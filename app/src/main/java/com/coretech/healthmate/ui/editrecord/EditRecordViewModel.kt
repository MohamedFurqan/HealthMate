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

    private val _recordNotFound = MutableLiveData<Boolean>()
    val recordNotFound: LiveData<Boolean> get() = _recordNotFound

    private var isSaving = false

    fun loadRecord(id: Int) {
        viewModelScope.launch {
            try {
                val record = withContext(Dispatchers.IO) {
                    repository.getRecordById(id)
                }
                if (record == null) {
                    _recordNotFound.value = true
                } else {
                    _loadedRecord.value = record
                    _recordNotFound.value = false
                }
            } catch (e: Exception) {
                _recordNotFound.value = true
            }
        }
    }

    fun updateRecord(date: String, stepsStr: String, caloriesStr: String, waterStr: String) {
        if (isSaving) return
        val currentRecord = _loadedRecord.value ?: return

        if (date.isBlank()) {
            _validationError.value = "Please select a date."
            return
        }
        val steps = validateNumber(stepsStr, "Steps", 100_000) ?: return
        val calories = validateNumber(caloriesStr, "Calories", 50_000) ?: return
        val water = validateNumber(waterStr, "Water", 20_000) ?: return

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