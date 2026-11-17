package com.coretech.healthmate.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Database Info
        private const val DATABASE_NAME = "healthmate.db"
        private const val DATABASE_VERSION = 1

        // Table Info
        const val TABLE_HEALTH_RECORDS = "health_records"

        // Column Names
        const val COLUMN_ID = "id"
        const val COLUMN_DATE = "date"
        const val COLUMN_STEPS = "steps"
        const val COLUMN_CALORIES = "calories"
        const val COLUMN_WATER = "water"
    }

    // This method is called ONLY when the database is created for the very first time
    override fun onCreate(db: SQLiteDatabase) {
        // 1. Create the table
        val createTableQuery = """
            CREATE TABLE $TABLE_HEALTH_RECORDS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DATE TEXT NOT NULL UNIQUE,
                $COLUMN_STEPS INTEGER NOT NULL,
                $COLUMN_CALORIES INTEGER NOT NULL,
                $COLUMN_WATER INTEGER NOT NULL
            )
        """.trimIndent()
        
        db.execSQL(createTableQuery)

        // 2. Insert the three initial dummy records
        insertInitialDummyData(db)
    }

    // This method is called ONLY when DATABASE_VERSION is increased
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // For Module 02, we will drop the old table and recreate it.
        // In a real production app, we would use ALTER TABLE to preserve user data.
        // But since our schema is simple, this is safe for this coursework.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HEALTH_RECORDS")
        onCreate(db)
    }

    // Helper method to insert the 3 initial records
    private fun insertInitialDummyData(db: SQLiteDatabase) {
        // Using hardcoded past dates to avoid clashing with "Today" when the user opens the app
        val dummyRecords = listOf(
            Triple("2026-08-01", 7500, 350),
            Triple("2026-08-02", 10200, 500),
            Triple("2026-08-03", 4000, 200)
        )

        for (record in dummyRecords) {
            val values = ContentValues().apply {
                put(COLUMN_DATE, record.first)
                put(COLUMN_STEPS, record.second)
                put(COLUMN_CALORIES, record.third)
                put(COLUMN_WATER, 2000) // 2000ml water for all dummy records
            }
            // Insert directly into the DB
            db.insert(TABLE_HEALTH_RECORDS, null, values)
        }
    }
}