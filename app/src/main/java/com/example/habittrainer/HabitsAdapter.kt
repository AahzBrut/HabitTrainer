package com.example.habittrainer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.single_card.view.*

class HabitsAdapter(private val habits: List<Habit>) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    class HabitViewHolder(val card: View) : RecyclerView.ViewHolder(card)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_card, parent, false)

        return HabitViewHolder(view)
    }

    override fun getItemCount() = habits.size

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {

        val habit = habits[position]
        val card = holder.card
        card.tv_title.text = habit.title
        card.tv_description.text = habit.description
        card.iv_icon.setImageBitmap(habit.image)
    }
}

