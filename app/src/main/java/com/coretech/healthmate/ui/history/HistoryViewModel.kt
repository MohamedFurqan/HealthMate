package com.coretech.healthmate.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.coretech.healthmate.data.model.HealthRecord
import com.coretech.healthmate.data.repository.HealthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HealthRepository(application)

    private val _records = MutableLiveData<List<HealthRecord>>()
    val records: LiveData<List<HealthRecord>> get() = _records

    private val _emptyStateMessage = MutableLiveData<String?>()
    val emptyStateMessage: LiveData<String?> get() = _emptyStateMessage

    fun loadAllRecords() {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getAllRecords()
                }
                _records.value = result

                if (result.isEmpty()) {
                    _emptyStateMessage.value = "No health records yet. Start tracking your daily activity!"
                } else {
                    _emptyStateMessage.value = null
                }
            } catch (e: Exception) {
                // Covers SQLiteException and any unexpected errors
                _records.value = emptyList()
                _emptyStateMessage.value = "Failed to load records. Please try again."
            }
        }
    }

    fun searchByDate(date: String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val record = repository.getRecordByDate(date)
                    if (record != null) listOf(record) else emptyList()
                }
                _records.value = result

                if (result.isEmpty()) {
                    _emptyStateMessage.value = "No health record found for this date."
                } else {
                    _emptyStateMessage.value = null
                }
            } catch (e: Exception) {
                _records.value = emptyList()
                _emptyStateMessage.value = "Search failed. Please try again."
            }
        }
    }

    fun clearSearch() {
        loadAllRecords()
    }
}