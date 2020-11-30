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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.databinding.FragmentTaskListBinding
import org.jglrxavpok.todo.task.TaskActivity
import org.jglrxavpok.todo.task.TaskActivity.Companion.ADD_TASK_REQUEST_CODE
import java.util.*
import kotlin.collections.ArrayList

class TaskListFragment: Fragment() {
    private val taskList = (1..5).map { Task("id_$it", "Task $it", "Description of $it") }.toMutableList() as ArrayList<Task>

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

        binding.addTaskButton.setOnClickListener {
            val intent = Intent(activity, TaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val task = data!!.getSerializableExtra("newTask") as Task
            if(data.getBooleanExtra("isNotNew", false)) {
                taskList.first{ it.id == task.id}.apply {
                    title = if(task.title.isNotBlank()) task.title else title
                    description = if(task.description.isNotBlank()) task.description else description
                }
            }
            else {
                taskList += task
            }
            val binding = DataBindingUtil.bind<FragmentTaskListBinding>(view!!)!!
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["setList"])
        fun RecyclerView.bindRecyclerViewAdapter(list: MutableList<Task>) {
            this.run {
                val activity = this.context as Activity
                var binding = DataBindingUtil.bind<FragmentTaskListBinding>(this.parent as View)
                this.setHasFixedSize(true)
                this.adapter = TaskListAdapter(list)
                (this.adapter as? TaskListAdapter)?.onDeleteClickListener = { task ->
                    list -= task
                    adapter!!.notifyDataSetChanged()
                }
                (this.adapter as? TaskListAdapter)?.onEditClickListener = { task ->
                    val intent = Intent(activity, TaskActivity::class.java)
                    intent.putExtra("currentTask", task)
                    activity.startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
                }
            }
        }
    }
}