# HealthMate Core

HealthMate Core is an Android health and wellness tracking application designed to help users record and monitor their daily activity. Users can track steps, calories, water intake and sleep, set personal goals, and monitor their progress over time. All health records are stored locally on the device.

## Features

- **Health records:** Add, view, edit and delete daily health records, with undo support for deletions. Records are stored locally using Room (SQLite).
- **History:** Browse previous health records, filter by date range and metric, view records grouped by day, and open individual record details.
- **Home dashboard:** View daily activity through a steps progress ring, metric tiles, animated water bottle, sleep dial and recent records.
- **Progress tracking:** Monitor health activity through Day, Week and Month charts.
- **Goals and targets:** Set personal targets for steps, calories, water intake, sleep and active days.
- **Gamification:** Build daily streaks by meeting your steps goal, earn achievement badges and receive in-app messages when new badges are unlocked.
- **Animations:** Includes a pulsing streak flame, filling water bottle, animated walking footprints on the steps ring and a sweeping sleep progress dial.
- **Light and dark mode:** The app follows the device's light or dark theme setting.

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose with Material 3
- **Database:** Room (SQLite)
- **Architecture:** MVVM with ViewModels and Kotlin Flow / StateFlow
- **Navigation:** Navigation Compose
- **Code generation:** Kotlin Symbol Processing (KSP) for Room
- **Unit testing:** JUnit with `RecordValidatorTest` and `GamificationCalculatorTest`
- **Instrumented testing:** AndroidX Test with `HealthRecordDaoTest` for Room database operations

## Architecture

HealthMate Core follows the Model-View-ViewModel (MVVM) architecture, separating data management, business logic (ViewModels) and the user interface.

```text
Room entity → DAO → Repository → ViewModel → Compose screen
   (table)   (queries) (data access) (StateFlow)    (UI)
```

- **`data`:** Entities, DAOs, repositories and the Room database.
- **`records`:** Records ViewModel, filters, validator and sleep calculator.
- **`home`, `history`, `progress`, `profile`, `details`, `recordform`:** Packages organised by screen.
- **`gamification`:** Streak and badge calculator and its ViewModel.
- **`navigation`:** NavHost, navigation routes and bottom navigation bar.
- **`ui/theme`:** Colours, typography and application themes.

## Database

HealthMate Core uses a Room database named `healthmate.db` (version 3), consisting of three tables:

| Table | Purpose |
|---|---|
| `health_records` | Stores health records, including date, time, steps, calories, water intake, sleep duration and notes. |
| `goals` | Stores the user's daily targets (one row) for steps, calories, water, sleep and active days. |
| `earned_badges` | Stores earned badges and the date each badge was first unlocked. |

**Database migrations:** Version 1 → 2 introduced the `goals` table, while version 2 → 3 introduced the `earned_badges` table. Both migrations use Room AutoMigrations to preserve existing records. Database schemas are exported to `app/schemas`.

## Getting started

**Requirements:** Android Studio (latest stable), and an emulator or Android device running Android 8.0 (API 26) or newer. The project uses Java 11, which is handled by Android Studio's built-in JDK.

1. Clone the repository:

   ```bash
   git clone https://github.com/Esandi-29/HealthMate-Core.git
   ```

2. Open the cloned project folder in Android Studio and wait for Gradle sync to finish.

3. Select an emulator or connected Android device, then click **Run**.

## Running tests

- **Unit tests:** Run `./gradlew test` to execute `RecordValidatorTest` and `GamificationCalculatorTest`.
- **Database tests:** Run `./gradlew connectedAndroidTest` to execute `HealthRecordDaoTest`. This requires a running Android emulator or connected device.

You can also run individual test classes by right-clicking the test class in Android Studio and selecting **Run**.
