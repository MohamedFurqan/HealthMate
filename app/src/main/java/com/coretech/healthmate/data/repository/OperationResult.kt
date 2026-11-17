package com.coretech.healthmate.data.repository

/**
 * A sealed class representing the outcome of a repository operation.
 * This allows the ViewModel to handle specific scenarios (like a duplicate date) cleanly.
 */
sealed class OperationResult {
    data class Success(val message: String = "Operation successful") : OperationResult()
    data class Error(val message: String) : OperationResult()
    data class DuplicateDate(val message: String = "A record already exists for this date.") : OperationResult()
    data class NotFound(val message: String = "Record not found.") : OperationResult()
}