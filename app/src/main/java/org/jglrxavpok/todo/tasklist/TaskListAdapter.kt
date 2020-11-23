package org.jglrxavpok.todo.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.jglrxavpok.todo.R

class TaskListAdapter(private val taskList: List<Task>): RecyclerView.Adapter<TaskListAdapter.Holder>() {

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(taskTitle: Task) {
            itemView.apply {
                val textView = findViewById<TextView>(R.id.task_title)
                textView.text = taskTitle.title

                val descriptionView = findViewById<TextView>(R.id.task_description)
                descriptionView.text = taskTitle.description
            }
        }
    }

    override fun getItemCount() = taskList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(taskList[position])
    }
}