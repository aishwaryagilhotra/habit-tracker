# 🌱 Habit Tracker 3

A beautifully designed **Habit Tracker App** that helps users build healthy habits, track progress, and stay consistent — all built using **Kotlin** in **Android Studio**.

---

## Features

* **User Authentication**

  * Signup, Login, and Forgot Password screens
  * Local data storage for user details

*  **Habit Tracking**

  * Add, edit, and delete habits
  * Track daily streaks and completions

*  **Garden View**

  * Visual growth garden representing your consistency

* **Statistics Dashboard**

  * Progress charts and habit performance visualization using `LineChartView.kt`

* **Settings**

  * Manage profile, view stats, and logout

*  **Offline Support**

  * Stores all data locally using **SQLite** (`DatabaseHelper.kt`)

---

##  Tech Stack

| Category     | Technology                                      |
| ------------ | ----------------------------------------------- |
| Language     | **Kotlin**                                      |
| IDE          | **Android Studio (Hedgehog / Ladybug)**         |
| Database     | **SQLite**                                      |
| Architecture | MVVM-like (ViewModel + Repository + Data Layer) |
| UI           | XML, RecyclerView, Fragments, Custom Views      |
| Tools        | Gradle, AndroidX, ViewBinding                   |

---

## Project Structure

```
main/
├── AndroidManifest.xml
├── java/com/example/habit_tracker_3/
│   ├── DatabaseHelper.kt
│   ├── MainActivity.kt
│   ├── HomeActivity.kt
│   ├── Models.kt
│   ├── HabitFragment.kt
│   ├── GardenFragment.kt
│   ├── StatsFragment.kt
│   ├── SettingsFragment.kt
│   ├── SignupFragment.kt
│   ├── Login_Fragment.kt
│   ├── ForgotPasswordFragment.kt
│   ├── LineChartView.kt
│   ├── data/
│   │   ├── LoginRepository.kt
│   │   ├── LoginDataSource.kt
│   │   ├── Result.kt
│   │   └── model/LoggedInUser.kt
│   └── ui/login/
│       ├── HabitAdapter.kt
│       ├── GardenAdapter.kt
│       ├── LoginViewModel.kt
│       ├── LoginViewModelFactory.kt
│       ├── LoginFormState.kt
│       ├── LoginResult.kt
│       └── LoggedInUserView.kt
└── res/
    ├── drawable/
    │   ├── ic_add.xml
    │   ├── ic_delete.xml
    │   ├── ic_edit.xml
    │   ├── ic_stats.xml
    │   └── plant.png
    ├── layout/
    └── values/
```


## Screenshots

|           Login Screen          |         Home Dashboard        |            Garden View            |
| :-----------------------------: | :---------------------------: | :-------------------------------: |
| ![Login](screenshots/login.png) | ![Home](screenshots/home.png) | ![Garden](screenshots/garden.png) |


---

## Future Improvements

* Sync with Firebase for cloud backup
* Add habit reminders and notifications
* Calendar-based progress view
* Daily motivational quotes
* Improved UI with Jetpack Compose

---
