package com.coretech.healthmate.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import com.coretech.healthmate.data.database.DatabaseHelper
import com.coretech.healthmate.data.model.HealthRecord

class HealthRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /**
     * Inserts a new health record into the database.
     */
    fun insertRecord(record: HealthRecord): OperationResult {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_DATE, record.date)
            put(DatabaseHelper.COLUMN_STEPS, record.steps)
            put(DatabaseHelper.COLUMN_CALORIES, record.calories)
            put(DatabaseHelper.COLUMN_WATER, record.water)
        }

        return try {
            val id = db.insertOrThrow(DatabaseHelper.TABLE_HEALTH_RECORDS, null, values)
            if (id != -1L) {
                OperationResult.Success("Record added successfully with ID: $id")
            } else {
                OperationResult.Error("Failed to insert record.")
            }
        } catch (e: SQLiteConstraintException) {
            // This catches the UNIQUE constraint error on the date column
            OperationResult.DuplicateDate()
        } catch (e: Exception) {
            OperationResult.Error("Database error: ${e.message}")
        } finally {
            db.close()
        }
    }

    /**
     * Retrieves all health records, ordered by date (newest first).
     */
    fun getAllRecords(): List<HealthRecord> {
        val db = dbHelper.readableDatabase
        val records = mutableListOf<HealthRecord>()
        
        // Order by date DESC (Newest first)
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_HEALTH_RECORDS,
            null, null, null, null, null,
            "${DatabaseHelper.COLUMN_DATE} DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                records.add(cursorToHealthRecord(cursor))
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return records
    }

    /**
     * Retrieves a single record by its ID.
     */
    fun getRecordById(id: Int): HealthRecord? {
        val db = dbHelper.readableDatabase
        var record: HealthRecord? = null

        val cursor = db.query(
            DatabaseHelper.TABLE_HEALTH_RECORDS,
            null,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            record = cursorToHealthRecord(cursor)
        }
        
        cursor.close()
        db.close()
        return record
    }

    /**
     * Retrieves a single record by its date.
     */
    fun getRecordByDate(date: String): HealthRecord? {
        val db = dbHelper.readableDatabase
        var record: HealthRecord? = null

        val cursor = db.query(
            DatabaseHelper.TABLE_HEALTH_RECORDS,
            null,
            "${DatabaseHelper.COLUMN_DATE} = ?",
            arrayOf(date),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            record = cursorToHealthRecord(cursor)
        }

        cursor.close()
        db.close()
        return record
    }

    /**
     * Updates an existing health record.
     */
    fun updateRecord(record: HealthRecord): OperationResult {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_DATE, record.date)
            put(DatabaseHelper.COLUMN_STEPS, record.steps)
            put(DatabaseHelper.COLUMN_CALORIES, record.calories)
            put(DatabaseHelper.COLUMN_WATER, record.water)
        }

        return try {
            val rowsAffected = db.update(
                DatabaseHelper.TABLE_HEALTH_RECORDS,
                values,
                "${DatabaseHelper.COLUMN_ID} = ?",
                arrayOf(record.id.toString())
            )

            if (rowsAffected > 0) {
                OperationResult.Success("Record updated successfully.")
            } else {
                OperationResult.NotFound("Update failed: Record not found.")
            }
        } catch (e: SQLiteConstraintException) {
            OperationResult.DuplicateDate()
        } catch (e: Exception) {
            OperationResult.Error("Database error: ${e.message}")
        } finally {
            db.close()
        }
    }

    /**
     * Deletes a health record by its ID.
     */
    fun deleteRecord(id: Int): OperationResult {
        val db = dbHelper.writableDatabase
        return try {
            val rowsAffected = db.delete(
                DatabaseHelper.TABLE_HEALTH_RECORDS,
                "${DatabaseHelper.COLUMN_ID} = ?",
                arrayOf(id.toString())
            )

            if (rowsAffected > 0) {
                OperationResult.Success("Record deleted successfully.")
            } else {
                OperationResult.NotFound("Delete failed: Record not found.")
            }
        } catch (e: Exception) {
            OperationResult.Error("Database error: ${e.message}")
        } finally {
            db.close()
        }
    }

    /**
     * Helper method to map a Cursor row to a HealthRecord object.
     */
    private fun cursorToHealthRecord(cursor: Cursor): HealthRecord {
        return HealthRecord(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
            date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)),
            steps = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STEPS)),
            calories = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CALORIES)),
            water = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WATER))
        )
    }
}