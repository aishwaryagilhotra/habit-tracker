package com.example.habit_tracker_3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GardenAdapter(
    private val habits: List<Habit>
) : RecyclerView.Adapter<GardenAdapter.GardenViewHolder>() {

    // ViewHolder for one plant card
    inner class GardenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHabitName: TextView = itemView.findViewById(R.id.textHabitName)
        val textStreak: TextView = itemView.findViewById(R.id.textStreak)
        val imagePlant: ImageView = itemView.findViewById(R.id.imagePlant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GardenViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.plant_card, parent, false)

        // Hide checkbox and delete button for garden view
        view.findViewById<View>(R.id.checkCompleted).visibility = View.GONE
        view.findViewById<View>(R.id.btnDelete).visibility = View.GONE

        return GardenViewHolder(view)
    }

    override fun onBindViewHolder(holder: GardenViewHolder, position: Int) {
        val habit = habits[position]

        // Set habit name and streak
        holder.textHabitName.text = habit.name
        holder.textStreak.text = "Streak: ${habit.streak} days"

        // Update plant image based on health
        holder.imagePlant.setImageResource(
            when {
                habit.health >= 0.7f -> R.drawable.plant_healthy
                habit.health >= 0.4f -> R.drawable.plant_wilting
                else -> R.drawable.plant_dead
            }
        )
    }

    override fun getItemCount(): Int = habits.size
}
