# Spending Monitor

An Android application built with **Jetpack Compose** to capture, track, and filter notifications from installed mobile apps.

---

## 🌟 Key Features

- **Notification Listener (`NotificationTrackerService`)**
    - Listens to incoming system notifications in real time.
    - Automatically extracts notification title, content text, timestamp, and package name.
    - Persists notification history locally using Room Database.

- **Background Process Persistence (`KeepAliveService`)**
    - Runs a lightweight Foreground Service to keep the notification listener process alive on aggressive OEM background management systems (e.g., Xiaomi/HyperOS).

- **App Selection (`ApplicationScreen`)**
    - Scans installed non-system applications on the device.
    - Let users toggle which applications should be monitored for notification.

- **Keyword Management (`SettingScreen` / `DetailScreen`)**
    - Define custom keywords (e.g., `spent`, `paid`, `debited`, `transfer`, `amount`) per monitored application to isolate financial notifications.

- **Notification History & Filtering (`NotificationHistoryScreen`)**
    - View logged notifications with date selection (`DatePickerDialog`).
    - Toggle **Filter App** to only display notifications from tracked applications.
    - Toggle **Filter Keyword** to filter notifications matching defined keywords.

---

## 🛠️ Tech Stack & Architecture

- **Language:** [Kotlin](https://kotlinlang.org/) (2.2.x)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Database:** [Room Database](https://developer.android.com/training/data-storage/room) with [KSP (Kotlin Symbol Processing)](https://kotlinlang.org/docs/ksp-overview.html)
- **System Services:** `NotificationListenerService` & `ForegroundService`

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio:** Ladybug (2024.2) or higher recommended
- **JDK:** Java 11 or higher
- **Android Device / Emulator:** Android 7.0 (API Level 24) or higher (Android 8.0+ recommended for foreground service features)

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd TestSpendingMonitor
   ```

2. **Open in Android Studio:**
    - Open Android Studio and select **Open**.
    - Navigate to the project directory and open it.

3. **Build the project:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on Device or Emulator:**
    - Deploy the app to a connected Android device or emulator.

---

## 🔑 Permissions & OEM Settings

### Required Permissions

- **Notification Listener Access:**
  The app requires `BIND_NOTIFICATION_LISTENER_SERVICE` permission to read notification content. Upon opening the app, if access is not yet granted, tap **Grant Notification Listener Permission** to navigate to Android Settings and enable access.

- **Foreground Service:**
  Uses `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_SPECIAL_USE` to prevent OS memory killers from dropping the listener service.

### OEM Optimizations (e.g., Xiaomi / HyperOS)

If background notification tracking stops unexpectedly on OEM ROMs:
1. Enable **Autostart** for Spending Monitor in OS App Settings.
2. Set Battery Saver mode for the app to **No Restrictions**.
3. Allow Notification Listener permission in **Special App Access**.

---

## 📁 Project Structure

```
app/src/main/java/com/example/testspendingmonitor/
├── MainActivity.kt                     # Entry activity with bottom navigation
├── NotificationTrackerService.kt       # NotificationListenerService capturing notifications
├── KeepAliveService.kt                 # Foreground service maintaining process lifecycle
├── ApplicationScreen.kt                # UI to toggle applications for monitoring
├── SettingScreen.kt                    # UI for configuring keywords per application
├── NotificationHistoryScreen.kt        # UI to view & filter captured notifications
├── appDatabase/                        # Room entity, DAO, and ViewModel for app configuration
│   ├── AppEntity.kt
│   └── AppViewModel.kt
└── notificationDatabase/               # Room entity, DAO, and ViewModel for notification logs
    ├── NotificationEntity.kt
    └── NotificationViewModel.kt
```

---