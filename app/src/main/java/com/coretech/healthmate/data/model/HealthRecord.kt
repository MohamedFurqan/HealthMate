package com.coretech.healthmate.data.model

/**
 * Data class representing a single health record.
 * This maps directly to a row in the 'health_records' SQLite table.
 */
data class HealthRecord(
    val id: Int = 0,           // Default 0 for new records (Auto-incremented by SQLite)
    val date: String,          // Format: YYYY-MM-DD
    val steps: Int,
    val calories: Int,
    val water: Int
)