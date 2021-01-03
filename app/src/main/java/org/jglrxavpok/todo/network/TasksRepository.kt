package org.jglrxavpok.todo.network

import org.jglrxavpok.todo.tasklist.Task
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TasksRepository : KoinComponent{
    private val tasksWebService by inject<TasksWebService>()

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