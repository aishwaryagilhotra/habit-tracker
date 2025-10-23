package com.example.habit_tracker_3

import DatabaseHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HabitFragment(
    private val habitList: MutableList<Habit>,
    private val userId: Int,
    private val dbHelper: DatabaseHelper
) : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var textNoHabits: TextView
    private lateinit var fabAddHabit: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habit, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewHabits)
        textNoHabits = view.findViewById(R.id.textNoHabits)
        fabAddHabit = view.findViewById(R.id.fabAddHabit)

        // Changed: removed userId parameter
        habitAdapter = HabitAdapter(habitList, dbHelper)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = habitAdapter

        fabAddHabit.setOnClickListener { showAddHabitDialog() }

        updateEmptyView()
        return view
    }

    private fun showAddHabitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add New Habit")

        val input = EditText(requireContext())
        input.hint = "Enter habit name"
        input.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                val habit = Habit(0L, name)
                dbHelper.addHabit(habit, userId)
                habitList.clear()
                habitList.addAll(dbHelper.getHabits(userId))
                habitAdapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(habitList.size - 1)
                updateEmptyView()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun updateEmptyView() {
        if (habitList.isEmpty()) {
            textNoHabits.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textNoHabits.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}