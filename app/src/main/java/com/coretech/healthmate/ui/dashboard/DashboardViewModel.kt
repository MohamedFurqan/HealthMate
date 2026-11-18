package com.coretech.healthmate.ui.dashboard

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HealthRepository(application)

    private val _todayRecord = MutableLiveData<HealthRecord?>()
    val todayRecord: LiveData<HealthRecord?> get() = _todayRecord

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun loadTodayRecord() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayDate = dateFormat.format(Date())

        viewModelScope.launch {
            try {
                val record = withContext(Dispatchers.IO) {
                    repository.getRecordByDate(todayDate)
                }
                _todayRecord.value = record
                _errorMessage.value = null
            } catch (e: Exception) {
                _todayRecord.value = null
                _errorMessage.value = "Could not load today's data. Please try again."
            }
        }
    }
}