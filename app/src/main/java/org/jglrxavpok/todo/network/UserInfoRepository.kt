package org.jglrxavpok.todo.network

import org.jglrxavpok.todo.tasklist.Task

class UserInfoRepository {
    private val userInfoWebService get()= Api.userWebService

    suspend fun getUserInfo() : UserInfo {
        return userInfoWebService.getInfo().body()!!
    }

    suspend fun updateUserInfo(userInfo : UserInfo) {
        userInfoWebService.updateUserInfo(userInfo)
    }
}