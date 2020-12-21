package org.jglrxavpok.todo.userinfo

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jglrxavpok.todo.network.UserInfo
import org.jglrxavpok.todo.network.UserInfoRepository

class UserInfoViewModel : ViewModel() {
    private val repository = UserInfoRepository()
    private val _userInfo = MutableLiveData<UserInfo>()
    private val _uploadingState = MutableLiveData(false)
    val userInfo: LiveData<UserInfo> = _userInfo
    val uploadingState: LiveData<Boolean> = _uploadingState

    fun refreshUserInfo() {
        viewModelScope.launch {
            _userInfo.value = repository.getUserInfo()
        }
    }

    fun editUserInfo(userInfo: UserInfo) {
        viewModelScope.launch {
            repository.updateUserInfo(userInfo)
            _userInfo.value = userInfo
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun updateAvatar(contentResolver: ContentResolver, image: Uri) {
        viewModelScope.launch {
            _uploadingState.value = true
            repository.updateAvatar(contentResolver, image)
            refreshUserInfo()
            _uploadingState.value = false
        }
    }
}