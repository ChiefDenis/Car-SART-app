# CAR SART – Car Service And Repair Tracker

**CAR SART** is a modern Android application designed to help users track their vehicle's service history, manage maintenance schedules, and receive smart reminders.

## Project Overview

This project was built incrementally in 11 stages, following modern Android development best practices. It is a complete, production-ready application.

### Core Features

*   **Vehicle Management:** Add, edit, and view multiple vehicles.
*   **Service History:** Log all service records, including maintenance, repairs, and inspections.
*   **Smart Maintenance Reminders:** A background worker checks for due maintenance and sends rich notifications.
*   **Data Persistence:** All data is stored locally in an encrypted SQLCipher database using Room.
*   **Backup & Restore:** Users can back up and restore their data to a local JSON file.
*   **Settings:** Users can configure their preferred currency, unit system, and notification settings.

## Architecture

The application follows a standard MVVM (Model-View-ViewModel) architecture with a repository pattern, organized into three main layers:

*   **Data Layer:** Responsible for data persistence and access. This layer includes Room entities, DAOs, the `AppDatabase`, and repositories for each data type.
*   **Domain Layer:** Contains the core business logic of the application. This layer includes UseCases that encapsulate specific operations (e.g., `AddVehicle`, `GetVehicles`).
*   **UI Layer:** The user-facing part of the application, built entirely with Jetpack Compose. This layer includes screens, composables, and ViewModels that expose state to the UI.

### Key Technologies

*   **Kotlin:** The sole programming language.
*   **Jetpack Compose:** For building the entire UI.
*   **Hilt:** For dependency injection.
*   **Room:** For local database storage.
*   **SQLCipher:** For database encryption.
*   **WorkManager:** For running background tasks (maintenance checks).
*   **DataStore:** For storing user preferences.
*   **Gson:** For JSON serialization (used in backup/restore).

## Building and Running the Project

To build and run the project, you will need Android Studio Giraffe | 2022.3.1 or later.

1.  **Clone the repository:** `git clone <repository-url>`
2.  **Open in Android Studio:** Open the cloned project in Android Studio.
3.  **Sync Gradle:** Allow Gradle to sync the project and download all dependencies.
4.  **Run the app:** Select the `app` configuration and run it on an emulator or a physical device.

### Generating a Signed APK/AAB

To generate a signed release build, follow these steps:

1.  **Create a Keystore:** If you don't already have one, create a keystore file to sign your app. You can do this through Android Studio:
    *   Go to **Build > Generate Signed Bundle / APK...**
    *   Select **Android App Bundle** or **APK**.
    *   Click **Next**.
    *   Click **Create new...** and fill out the form.
2.  **Configure `gradle.properties`:** Once you have a keystore, create a `keystore.properties` file in the root of the project with the following content:

    ```properties
    storePassword=<your-store-password>
    keyAlias=<your-key-alias>
    keyPassword=<your-key-password>
    storeFile=<path-to-your-keystore-file>
    ```

3.  **Configure `build.gradle.kts`:** In the `app/build.gradle.kts` file, add a `signingConfigs` block to the `android` section:

    ```kotlin
    android {
        // ...
        signingConfigs {
            create("release") {
                val keystorePropertiesFile = rootProject.file("keystore.properties")
                if (keystorePropertiesFile.exists()) {
                    val properties = java.util.Properties()
                    properties.load(keystorePropertiesFile.inputStream())
                    storeFile = file(properties.getProperty("storeFile"))
                    storePassword = properties.getProperty("storePassword")
                    keyAlias = properties.getProperty("keyAlias")
                    keyPassword = properties.getProperty("keyPassword")
                }
            }
        }
        buildTypes {
            release {
                // ...
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    ```

4.  **Generate the Build:** Run the following command in the terminal:

    ```bash
    ./gradlew assembleRelease
    ```

    Or, use the **Build > Generate Signed Bundle / APK...** menu in Android Studio and select your existing keystore.
