package org.jglrxavpok.todo.network

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jglrxavpok.todo.tasklist.Task
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserInfoRepository : KoinComponent {
    private val userInfoWebService by inject<UserWebService>()

    suspend fun getUserInfo() : UserInfo {
        return userInfoWebService.getInfo().body()!!
    }

    suspend fun updateUserInfo(userInfo : UserInfo) {
        userInfoWebService.updateUserInfo(userInfo)
    }

    // convert file to HTTP body
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun convertFileForHTTP(contentResolver: ContentResolver, uri: Uri) = MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = contentResolver.openInputStream(uri)!!.readBytes().toRequestBody()
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun updateAvatar(contentResolver: ContentResolver, uri: Uri) {
        userInfoWebService.updateAvatar(convertFileForHTTP(contentResolver, uri))
    }
}