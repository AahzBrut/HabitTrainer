package com.example.habittrainer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

data class Habit(val title: String, val description: String, val image: Bitmap)

fun getInitialHabits(context: Context): List<Habit> {

    return listOf(
        Habit(
            "Go for a walk",
            "A nice walk in the sun gets you ready for the day ahead",
            BitmapFactory.decodeResource(context.resources, R.drawable.walk)
        ),
        Habit(
            "Drink a glass of water",
            "A refreshing glass of water get you hydrated",
            BitmapFactory.decodeResource(context.resources, R.drawable.water)
        )
    )
}

