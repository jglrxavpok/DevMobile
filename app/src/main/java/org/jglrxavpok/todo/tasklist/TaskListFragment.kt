package org.jglrxavpok.todo.tasklist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import kotlinx.coroutines.launch
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.databinding.FragmentTaskListBinding
import org.jglrxavpok.todo.network.Api
import org.jglrxavpok.todo.task.TaskActivity
import org.jglrxavpok.todo.task.TaskActivity.Companion.ADD_TASK_REQUEST_CODE
import org.jglrxavpok.todo.userinfo.UserInfoActivity

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

        binding.avatar.setOnClickListener {
            val intent = Intent(activity, UserInfoActivity::class.java)
            startActivity(intent)
        }

        viewModel.taskList.observe(viewLifecycleOwner) { newList ->
            adapter.taskList = newList.orEmpty()
        }
    }

    override fun onResume() {
        super.onResume()
        val binding = DataBindingUtil.bind<FragmentTaskListBinding>(view!!)!!
        binding.avatar.load("https://goo.gl/gEgYUd")
        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()!!
            val binding = DataBindingUtil.bind<FragmentTaskListBinding>(view!!)!!
            binding.name.text = "${userInfo.firstName} ${userInfo.lastName}"
            userInfo.avatarURL?.let {
                binding.avatar.load(it)
            }
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