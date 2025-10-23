package com.example.habit_tracker_3

import DatabaseHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val dbHelper: DatabaseHelper
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    // ViewHolder for one habit/plant card
    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHabitName: TextView = itemView.findViewById(R.id.textHabitName)
        val textStreak: TextView = itemView.findViewById(R.id.textStreak)
        val imagePlant: ImageView = itemView.findViewById(R.id.imagePlant)
        val checkCompleted: CheckBox = itemView.findViewById(R.id.checkCompleted)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.plant_card, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

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

        // Reset checkbox listener to avoid recycling issues
        holder.checkCompleted.setOnCheckedChangeListener(null)
        holder.checkCompleted.isChecked = false


        holder.checkCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                habit.streak++
                habit.health = (habit.health + 0.2f).coerceAtMost(1f)
            } else {
                // Missed habit - health decreases faster
                habit.health = (habit.health - 0.3f).coerceAtLeast(0f)
            }
            dbHelper.updateHabit(habit) // Persist changes
            notifyItemChanged(position)
        }

        holder.btnDelete.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                dbHelper.deleteHabit(habit.id) // Delete from DB
                habits.removeAt(pos)
                notifyItemRemoved(pos)
            }
        }
    }

    override fun getItemCount(): Int = habits.size
}