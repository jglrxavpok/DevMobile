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
import androidx.fragment.app.viewModels
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
    private val adapter = TaskListAdapter()
    private val viewModel by viewModels<TaskListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_list, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DataBindingUtil.bind<FragmentTaskListBinding>(view)!!
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)

        recyclerView.adapter = adapter
        adapter.onDeleteClickListener = { task ->
            viewModel.deleteTask(task)
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

        viewModel.taskList.observe(viewLifecycleOwner) {
            newList ->
            adapter.taskList = newList.orEmpty()
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
        viewModel.loadTasks()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val task = data!!.getSerializableExtra("newTask") as Task
            if(data.getBooleanExtra("isNotNew", false)) {
                viewModel.editTask(task)
            }
            else {
                viewModel.addTask(task)
            }
        }
    }
}