package org.jglrxavpok.todo.task

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.databinding.ActivityMainBinding
import org.jglrxavpok.todo.databinding.TaskActivityBinding
import org.jglrxavpok.todo.tasklist.Task
import java.util.*

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: TaskActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.task_activity)
        val isNotNew = intent.hasExtra("currentTask")
        val task = intent.getSerializableExtra("currentTask") as? Task ?: Task(id = UUID.randomUUID().toString())
        binding.validate.setOnClickListener{
            if(binding.editTitle.text.toString().isNotBlank()){
                task.title = binding.editTitle.text.toString()
            }
            if(binding.editDescription.text.toString().isNotBlank()){
                task.description = binding.editDescription.text.toString()
            }
            intent.putExtra("newTask", task)
            intent.putExtra("isNotNew", isNotNew)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    companion object {
        const val ADD_TASK_REQUEST_CODE = 666
    }
}

