# 🌱 Habit Tracker 

A modern and interactive **Habit Tracker App** built with **Android Studio (Kotlin)** to help users build better habits, visualize their progress, and stay motivated.

---

## ✨ Features

* 🔐 **User Authentication**

  * Signup, Login, and Forgot Password functionality
  * Secure local data storage using SQLite

* 📅 **Habit Management**

  * Add, edit, or delete habits
  * Track daily progress
  * Maintain streaks and performance overview

* 🌿 **Garden View**

  * Visual representation of your growth journey — each habit adds to your garden!

* 📊 **Statistics Dashboard**

  * Displays habit completion data using charts and streak metrics

* ⚙️ **Settings**

  * Profile management and logout options

* 🧠 **Offline Support**

  * Data stored locally using SQLite DatabaseHelper

---

## 🧩 Tech Stack

| Category      | Technology                                                    |
| ------------- | ------------------------------------------------------------- |
| Language      | **Kotlin**                                                    |
| IDE           | **Android Studio**                                            |
| Database      | **SQLite (via DatabaseHelper.kt)**                            |
| UI Components | **Fragments, RecyclerView, Custom Views (LineChartView)**     |
| Architecture  | **MVVM-like structure (ViewModel, Repository, Data classes)** |

---

## 📂 Folder Structure

```
src/
├── main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/habit_tracker_3/
│   │   ├── DatabaseHelper.kt
│   │   ├── MainActivity.kt
│   │   ├── HomeActivity.kt
│   │   ├── Models.kt
│   │   ├── Fragments/
│   │   │   ├── HabitFragment.kt
│   │   │   ├── GardenFragment.kt
│   │   │   ├── StatsFragment.kt
│   │   │   ├── SettingsFragment.kt
│   │   │   ├── SignupFragment.kt
│   │   │   ├── Login_Fragment.kt
│   │   │   └── ForgotPasswordFragment.kt
│   │   ├── ui/login/
│   │   │   ├── HabitAdapter.kt
│   │   │   ├── GardenAdapter.kt
│   │   │   ├── LoginViewModel.kt
│   │   │   ├── LoginRepository.kt
│   │   │   └── LoginDataSource.kt
│   └── res/
│       ├── drawable/
│       ├── layout/
│       └── values/
```

---

## 🚀 Getting Started

### 1️⃣ Clone this repository

```bash
git clone https://github.com/yourusername/habit-tracker.git
```

### 2️⃣ Open in Android Studio

* Open **Android Studio**
* Choose **Open an existing project**
* Select the cloned folder

### 3️⃣ Build and Run

* Connect your Android device or launch an emulator
* Click **Run ▶️**

---

## 🖼️ Screenshots (Optional)

|              Login              |              Home             |              Stats              |
| :-----------------------------: | :---------------------------: | :-----------------------------: |
| ![Login](screenshots/login.png) | ![Home](screenshots/home.png) | ![Stats](screenshots/stats.png) |

---

## 🧠 Future Enhancements

* ☁️ Sync data with Firebase
* 🔔 Add daily habit reminders
* 🧘‍♀️ Introduce motivational quotes
* 📅 Calendar-based progress tracking

