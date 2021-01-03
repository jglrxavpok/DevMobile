package org.jglrxavpok.todo.tasklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jglrxavpok.todo.network.TasksRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TaskListViewModel : ViewModel(), KoinComponent {
    private val modifiableTaskList = MutableLiveData<List<Task>>()
    private val addTask = AddTask()
    val taskList: LiveData<List<Task>> = modifiableTaskList
    private val repository by inject<TasksRepository>()

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
            modifiableTaskList.value = addTask.execute(modifiableTaskList.value, repository, task)
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