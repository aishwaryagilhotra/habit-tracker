package com.example.habit_tracker_3

import android.graphics.Bitmap

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val gender: String,
    val profilePic: Bitmap?
)

data class Habit(
    val id: Long,             // SQLite row ID
    val name: String,
    var streak: Int = 0,      // Default streak is 0
    var health: Float = 1.0f  // 1.0 = healthy, 0 = dead
)
