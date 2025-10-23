package com.example.habit_tracker_3

import DatabaseHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GardenFragment(
    private val userId: Int,
    private val dbHelper: DatabaseHelper
) : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gardenAdapter: GardenAdapter
    private lateinit var textNoPlants: TextView
    private var habitList: MutableList<Habit> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_garden, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewGarden)
        textNoPlants = view.findViewById(R.id.textNoPlants)

        habitList = dbHelper.getHabits(userId)
        gardenAdapter = GardenAdapter(habitList)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = gardenAdapter

        updateEmptyView()
        return view
    }

    private fun updateEmptyView() {
        textNoPlants.visibility = if (habitList.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (habitList.isEmpty()) View.GONE else View.VISIBLE
    }

    fun refreshHabits() {
        habitList.clear()
        habitList.addAll(dbHelper.getHabits(userId))
        gardenAdapter.notifyDataSetChanged()
        updateEmptyView()
    }
}
