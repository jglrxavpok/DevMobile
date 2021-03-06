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
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.auth.AuthenticationActivity
import org.jglrxavpok.todo.databinding.FragmentTaskListBinding
import org.jglrxavpok.todo.task.TaskActivity
import org.jglrxavpok.todo.task.TaskActivity.Companion.ADD_TASK_REQUEST_CODE
import org.jglrxavpok.todo.userinfo.UserInfoActivity
import org.jglrxavpok.todo.userinfo.UserInfoViewModel

class TaskListFragment: Fragment() {
    private val adapter = TaskListAdapter()
    private val viewModel by viewModels<TaskListViewModel>()
    private val userViewModel by viewModels<UserInfoViewModel>()

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

        if(!userViewModel.isLoggedIn()) {
            onDisconnect()
            return
        }

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

        binding.disconnectButton.setOnClickListener {
            userViewModel.disconnect(context)
            onDisconnect()
        }

        viewModel.taskList.observe(viewLifecycleOwner) { newList ->
            adapter.taskList = newList.orEmpty()
        }
    }

    /**
     * Called when the user is disconnected (even when entering this fragment with no user info)
     */
    private fun onDisconnect() {
        val intent = Intent(activity, AuthenticationActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val binding = DataBindingUtil.bind<FragmentTaskListBinding>(view!!)!!
        binding.avatar.load("https://goo.gl/gEgYUd")

        userViewModel.userInfo.observe(this) { userInfo ->
            if(userInfo == null) {
                onDisconnect()
            } else {
                val binding = DataBindingUtil.bind<FragmentTaskListBinding>(view!!)!!
                binding.name.text = "${userInfo.firstName} ${userInfo.lastName}"
                userInfo.avatarURL?.let {
                    binding.avatar.load(it)
                }
            }
        }
        userViewModel.refreshUserInfo()
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