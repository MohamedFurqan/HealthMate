# HealthMate - Personal Health & Wellness Tracker

A native Android application developed as part of a BSc (Hons) Software Engineering coursework.

## Project Overview
HealthMate allows users to record, manage, search, and review their daily health activities (steps, calories burned, and water intake). The application works entirely offline and stores data locally using SQLite.

## Features
- **Dashboard:** View today's steps, calories, and water intake at a glance.
- **Add Record:** Create a new health record with date, steps, calories, and water.
- **History:** View all saved records, ordered newest first.
- **Search:** Filter records by date using a native date picker.
- **Edit:** Update existing records with validation.
- **Delete:** Remove records with a confirmation dialog.
- **Validation:** Negative values, empty fields, duplicate dates, and overly large integers are rejected.
- **Offline:** All data is stored locally in SQLite; no internet required.

## Technology Stack
| Component | Technology |
|---|---|
| Language | Kotlin |
| UI | XML Layouts |
| Platform | Native Android |
| Database | SQLite (via `SQLiteOpenHelper`) |
| Architecture | MVVM |
| Build System | Gradle (Kotlin DSL) |
| IDE | Visual Studio Code |

### Package Structure
- `data/database/` — `DatabaseHelper` (table creation, dummy data)
- `data/model/` — `HealthRecord` data class
- `data/repository/` — `HealthRepository`, `OperationResult`
- `ui/dashboard/` — Dashboard screen
- `ui/addrecord/` — Add Record screen
- `ui/history/` — History list + search
- `ui/editrecord/` — Edit/Delete screen

## Database
**Table:** `health_records`

| Column | Type | Constraint |
|---|---|---|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT |
| date | TEXT | NOT NULL, UNIQUE |
| steps | INTEGER | NOT NULL |
| calories | INTEGER | NOT NULL |
| water | INTEGER | NOT NULL |

Three dummy records are seeded on first database creation for demonstration.

## Prerequisites
- JDK 21
- Android SDK (API 34)
- Gradle Wrapper (included)
- Android device or emulator (API 24+)

## Build Instructions
1. Clone the repository.
2. Set `ANDROID_HOME` to your Android SDK path.
3. Build the debug APK:
   ```bash
   ./gradlew assembleDebug