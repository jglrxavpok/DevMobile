package org.jglrxavpok.todo.tasklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jglrxavpok.todo.network.TasksRepository

class TaskListViewModel: ViewModel() {
    private val modifiableTaskList = MutableLiveData<List<Task>>()

    val taskList: LiveData<List<Task>> = modifiableTaskList
    private val repository = TasksRepository()

    fun loadTasks() {
        viewModelScope.launch {
            modifiableTaskList.value = repository.refresh()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            modifiableTaskList.value = modifiableTaskList.value?.filterNot { it == task }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
            modifiableTaskList.value = modifiableTaskList.value?.plus(task)
        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            modifiableTaskList.value?.let {
                it.first{ it.id == task.id}.apply {
                    title = if(task.title.isNotBlank()) task.title else title
                    description = if(task.description.isNotBlank()) task.description else description
                }
            }
        }
    }
}