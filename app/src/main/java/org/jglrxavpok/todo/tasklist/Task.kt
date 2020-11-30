package org.jglrxavpok.todo.tasklist
import java.io.Serializable

data class Task(val id: String, var title: String = "New Task !", var description: String = "New Task Description !") : Serializable