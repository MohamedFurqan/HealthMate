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

## Architecture
The application follows the **MVVM** (Model-View-ViewModel) pattern:
