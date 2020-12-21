package org.jglrxavpok.todo.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.jglrxavpok.todo.tasklist.Task

class TasksRepository {
    private val tasksWebService get()= Api.Instance.taskService

    suspend fun refresh(): List<Task>? {
        val freshData = tasksWebService.getTasks()
        return if(freshData.isSuccessful) freshData.body() else null

    }

    suspend fun updateTask(task: Task) {
        tasksWebService.updateTask(task)
    }

    suspend fun addTask(task: Task) {
        tasksWebService.createTask(task)
    }

    suspend fun deleteTask(task: Task) {
        tasksWebService.deleteTask(task.id)
    }
}