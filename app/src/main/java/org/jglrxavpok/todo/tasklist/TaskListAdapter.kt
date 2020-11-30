package org.jglrxavpok.todo.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.databinding.ItemTaskBinding

class TaskListAdapter(taskList: List<Task>): ListAdapter<Task, TaskListAdapter.Holder>(DiffCallback) {

    var onDeleteClickListener: ((Task) -> Unit)? = null

    init {
        submitList(taskList)
    }

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            itemView.apply {
                val binding = DataBindingUtil.bind<ItemTaskBinding>(this)!!
                binding.task = task
                binding.delete.setOnClickListener {
                    onDeleteClickListener?.invoke(task)
                    println(onDeleteClickListener)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DiffCallback = object: DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
        }
    }
}