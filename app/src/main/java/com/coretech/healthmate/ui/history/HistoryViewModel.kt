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

    // Observed by the Fragment to update the RecyclerView
    private val _records = MutableLiveData<List<HealthRecord>>()
    val records: LiveData<List<HealthRecord>> get() = _records

    // Observed by the Fragment to show/hide the empty state
    private val _emptyStateMessage = MutableLiveData<String?>()
    val emptyStateMessage: LiveData<String?> get() = _emptyStateMessage

    // Tracks whether the user is currently searching
    private var isSearching = false

    fun loadAllRecords() {
        isSearching = false
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getAllRecords()
            }
            _records.value = result
            
            // If database is truly empty, show general empty state
            if (result.isEmpty()) {
                _emptyStateMessage.value = "No health records yet. Start tracking your daily activity!"
            } else {
                _emptyStateMessage.value = null
            }
        }
    }

    fun searchByDate(date: String) {
        isSearching = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                val record = repository.getRecordByDate(date)
                if (record != null) listOf(record) else emptyList()
            }
            _records.value = result

            // If search returns nothing, show search-specific empty state
            if (result.isEmpty()) {
                _emptyStateMessage.value = "No health record found for this date."
            } else {
                _emptyStateMessage.value = null
            }
        }
    }

    fun clearSearch() {
        loadAllRecords()
    }
}