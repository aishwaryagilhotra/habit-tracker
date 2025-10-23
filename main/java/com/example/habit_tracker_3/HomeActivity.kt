package com.example.habit_tracker_3

import DatabaseHelper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var currentUser: User
    private lateinit var gardenFragment: GardenFragment
    private lateinit var habitFragment: HabitFragment
    private lateinit var statsFragment: StatsFragment
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        dbHelper = DatabaseHelper(this)
        currentUser = dbHelper.getCurrentUser(this) ?: run { finish(); return }

        // Load habits from DB
        val habitList = dbHelper.getHabits(currentUser.id)

        gardenFragment = GardenFragment(currentUser.id, dbHelper)
        habitFragment = HabitFragment(habitList, currentUser.id, dbHelper) // pass DB + userId
        statsFragment = StatsFragment(habitList, currentUser.id, dbHelper)
        settingsFragment = SettingsFragment(currentUser.id, dbHelper, habitList)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        if (savedInstanceState == null) loadFragment(gardenFragment)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_garden -> loadFragment(gardenFragment)
                R.id.nav_habit -> loadFragment(habitFragment)
                R.id.nav_stats -> loadFragment(statsFragment)
                R.id.nav_settings -> loadFragment(settingsFragment)
            }
            true
        }
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
