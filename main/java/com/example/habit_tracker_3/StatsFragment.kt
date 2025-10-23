package com.example.habit_tracker_3

import DatabaseHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class StatsFragment(
    private val habitList: MutableList<Habit>,
    private val userId: Int,
    private val dbHelper: DatabaseHelper
) : Fragment() {

    private lateinit var textTotalHabits: TextView
    private lateinit var textAvgStreak: TextView
    private lateinit var chartView: LineChartView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        textTotalHabits = view.findViewById(R.id.textTotalHabits)
        textAvgStreak = view.findViewById(R.id.textAvgStreak)
        chartView = view.findViewById(R.id.chartView)

        updateStats()
        return view
    }

    fun updateStats() {
        val total = habitList.size
        val avgStreak = if (total > 0) habitList.sumOf { it.streak }.toDouble() / total else 0.0
        textTotalHabits.text = "Total Habits: $total"
        textAvgStreak.text = "Average Streak: ${String.format("%.1f", avgStreak)} days"

        chartView.setData(habitList)
    }
}