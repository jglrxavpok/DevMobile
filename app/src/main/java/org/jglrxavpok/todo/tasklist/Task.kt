package org.jglrxavpok.todo.tasklist
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    @SerialName("id")
    val id: String,

    @SerialName("title")
    var title: String = "New Task !",

    @SerialName("description")
    var description: String = "New Task Description !"): java.io.Serializable