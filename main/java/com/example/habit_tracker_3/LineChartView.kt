package com.example.habit_tracker_3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var habitList: List<Habit> = emptyList()

    // Paints
    private val paintBar = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 36f
        textAlign = Paint.Align.CENTER
    }
    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.MAGENTA
        strokeWidth = 6f
    }

    // Cute color palette for bars
    private val colors = listOf(
        Color.parseColor("#FFB6C1"), // light pink
        Color.parseColor("#87CEFA"), // light blue
        Color.parseColor("#90EE90"), // light green
        Color.parseColor("#FFA07A"), // light orange
        Color.parseColor("#DDA0DD"), // plum
        Color.parseColor("#FFE4B5")  // moccasin
    )

    fun setData(list: List<Habit>) {
        habitList = list
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (habitList.isEmpty()) return

        val widthPerItem = width / (habitList.size + 1f)
        val maxStreak = (habitList.maxOf { it.streak }).coerceAtLeast(1)
        val heightFactor = height.toFloat() / (maxStreak + 3) // leave space for numbers and names

        var prevX = 0f
        var prevY = 0f

        habitList.forEachIndexed { index, habit ->
            val left = (index + 0.5f) * widthPerItem
            val top = height - habit.streak * heightFactor - 40 // leave margin for text
            val right = left + widthPerItem / 2
            val bottom = height - 60f // leave space for habit name

            // Assign color from palette
            paintBar.color = colors[index % colors.size]

            // Draw bar
            canvas.drawRect(left, top, right, bottom, paintBar)

            // Draw streak number above bar
            canvas.drawText("${habit.streak}", left + widthPerItem / 4, top - 10f, paintText)

            // Draw line connecting tops
            val centerX = left + widthPerItem / 4
            val centerY = top
            if (index > 0) {
                canvas.drawLine(prevX, prevY, centerX, centerY, paintLine)
            }
            prevX = centerX
            prevY = centerY

            // Draw habit name below bar
            canvas.drawText(
                habit.name,
                left + widthPerItem / 4,
                height - 20f,
                paintText
            )
        }
    }
}
