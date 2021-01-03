package org.jglrxavpok.todo.tasklist

import org.jglrxavpok.todo.network.TasksRepository

class AddTask {
    suspend fun execute(taskList : List<Task>?, repository : TasksRepository, task : Task): List<Task>? {
        repository.addTask(task)
        return taskList?.plus(task)
    }
}