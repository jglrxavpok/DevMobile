package org.jglrxavpok.todo.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignupForm(
        @SerialName("email")
        val email: String,

        @SerialName("firstname")
        val firstName: String,

        @SerialName("lastname")
        val lastName: String,

        @SerialName("password")
        val password: String,

        @SerialName("password_confirmation")
        val confirmPassword: String,
)
