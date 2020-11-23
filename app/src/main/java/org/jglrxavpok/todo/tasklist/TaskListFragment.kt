package org.jglrxavpok.todo.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jglrxavpok.todo.R
import java.util.*

class TaskListFragment: Fragment() {
    private val taskList = (1..5).map { Task("id_$it", "Task $it", "Description of $it") }.toMutableList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_list, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = TaskListAdapter(taskList)
        recyclerView.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.add_task_button).setOnClickListener {
            taskList += Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}", description = "Some description...")
            adapter.notifyDataSetChanged()
        }
    }
}