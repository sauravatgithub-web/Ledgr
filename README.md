# Ledgr

Local INR money tracker for Android — budgets, expenses, categories, and month-end reports.

Built with Kotlin + Jetpack Compose. Data stays on-device; CSV export included.

## Open in Android Studio

1. Install [Android Studio](https://developer.android.com/studio) if needed
2. **File → Open** → `~/projects/money-tracker`
3. Let Gradle sync (first sync downloads the Gradle wrapper and SDK packages)
4. Run on an emulator or device

If CLI build is needed later: in Android Studio use **Gradle → wrapper** or run `gradle wrapper --gradle-version 8.9` once Gradle is installed, then `./gradlew :app:assembleDebug`.

## Project layout

- `domain/` — models, `MoneyRepository` interface, use cases
- `data/` — Room implementation
- `ui/` — Compose screens
- `di/` — `AppContainer` wiring
