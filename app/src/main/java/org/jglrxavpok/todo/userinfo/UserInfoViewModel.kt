package org.jglrxavpok.todo.userinfo

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch
import org.jglrxavpok.todo.SHARED_PREF_TOKEN_KEY
import org.jglrxavpok.todo.network.Api
import org.jglrxavpok.todo.network.UserInfo
import org.jglrxavpok.todo.network.UserInfoRepository

class UserInfoViewModel : ViewModel() {
    private val repository = UserInfoRepository()
    private val _userInfo = MutableLiveData<UserInfo>()
    private val _uploadingState = MutableLiveData(false)
    val userInfo: LiveData<UserInfo> = _userInfo
    val uploadingState: LiveData<Boolean> = _uploadingState

    fun isLoggedIn() = Api.Instance.hasToken()

    fun refreshUserInfo() {
        viewModelScope.launch {
            _userInfo.value = repository.getUserInfo()
        }
    }

    fun disconnect(context: Context?) {
        _userInfo.value = null
        PreferenceManager.getDefaultSharedPreferences(context).edit() {
            putString(SHARED_PREF_TOKEN_KEY, "")
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