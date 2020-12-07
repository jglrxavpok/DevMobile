package org.jglrxavpok.todo.tasklist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.databinding.FragmentTaskListBinding
import org.jglrxavpok.todo.network.Api
import org.jglrxavpok.todo.network.TasksRepository
import org.jglrxavpok.todo.task.TaskActivity
import org.jglrxavpok.todo.task.TaskActivity.Companion.ADD_TASK_REQUEST_CODE
import java.util.*
import kotlin.collections.ArrayList

class TaskListFragment: Fragment() {
    private val tasksRepo = TasksRepository()
    private val taskList = ArrayList<Task>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_list, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DataBindingUtil.bind<FragmentTaskListBinding>(view)!!
        binding.tasks = taskList
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val adapter = TaskListAdapter(taskList)
        recyclerView.adapter = adapter
        adapter.onDeleteClickListener = { task ->
            lifecycleScope.launch {
                tasksRepo.deleteTask(task)
                tasksRepo.refresh()
            }
        }
        adapter.onEditClickListener = { task ->
            val intent = Intent(activity, TaskActivity::class.java)
            intent.putExtra("currentTask", task)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

        binding.addTaskButton.setOnClickListener {
            val intent = Intent(activity, TaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

        tasksRepo.taskList.observe(viewLifecycleOwner) {
            taskList.clear()
            taskList += it
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val userInfo = Api.userService.getInfo().body()!!
            val binding = DataBindingUtil.bind<FragmentTaskListBinding>(view!!)!!
            binding.name.text = "${userInfo.firstName} ${userInfo.lastName}"
        }

        lifecycleScope.launch {
            tasksRepo.refresh()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val task = data!!.getSerializableExtra("newTask") as Task
            if(data.getBooleanExtra("isNotNew", false)) {
                val editedTask = taskList.first{ it.id == task.id}.apply {
                    title = if(task.title.isNotBlank()) task.title else title
                    description = if(task.description.isNotBlank()) task.description else description
                }
                lifecycleScope.launch {
                    tasksRepo.updateTask(editedTask)
                    tasksRepo.refresh()
                }
            }
            else {
                lifecycleScope.launch {
                    tasksRepo.addTask(task)
                    tasksRepo.refresh()
                }
            }
        }
    }
}