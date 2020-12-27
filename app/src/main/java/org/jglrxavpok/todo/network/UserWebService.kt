package org.jglrxavpok.todo.network

import okhttp3.MultipartBody
import org.jglrxavpok.todo.auth.LoginForm
import org.jglrxavpok.todo.auth.LoginResponse
import org.jglrxavpok.todo.auth.SignupForm
import retrofit2.Response
import retrofit2.http.*

interface UserWebService {
    @GET("users/info")
    suspend fun getInfo(): Response<UserInfo>

    @Multipart
    @PATCH("users/update_avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): Response<UserInfo>

    @PATCH("users")
    suspend fun updateUserInfo(@Body userInfo: UserInfo): Response<UserInfo>

    @POST("users/login")
    suspend fun login(@Body user: LoginForm): Response<LoginResponse>

    @POST("users/sign_up")
    suspend fun signup(@Body user: SignupForm): Response<LoginResponse>
}