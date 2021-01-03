package org.jglrxavpok.todo.network

import org.jglrxavpok.todo.tasklist.Task
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface TasksRepository {
    suspend fun refresh(): List<Task>?
    suspend fun updateTask(task: Task)
    suspend fun addTask(task: Task)
    suspend fun deleteTask(task: Task)
}

class TasksRepositoryImpl : TasksRepository, KoinComponent{
    private val tasksWebService by inject<TasksWebService>()

    override suspend fun refresh(): List<Task>? {
        val freshData = tasksWebService.getTasks()
        return if(freshData.isSuccessful) freshData.body() else null

    }

    override suspend fun updateTask(task: Task) {
        tasksWebService.updateTask(task)
    }

    override suspend fun addTask(task: Task) {
        tasksWebService.createTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        tasksWebService.deleteTask(task.id)
    }
}