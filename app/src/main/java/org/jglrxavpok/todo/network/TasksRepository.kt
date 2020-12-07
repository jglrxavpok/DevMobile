package org.jglrxavpok.todo.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.jglrxavpok.todo.tasklist.Task

class TasksRepository {
    private val tasksWebService get()= Api.taskService

    private val modifiableTaskList = MutableLiveData<List<Task>>()

    val taskList: LiveData<List<Task>> = modifiableTaskList

    suspend fun refresh() {
        val freshData = tasksWebService.getTasks()
        if(freshData.isSuccessful) {
            modifiableTaskList.value = freshData.body()!!
        }
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