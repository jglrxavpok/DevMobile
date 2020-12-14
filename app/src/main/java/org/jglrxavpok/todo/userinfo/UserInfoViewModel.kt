package org.jglrxavpok.todo.userinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jglrxavpok.todo.network.UserInfo
import org.jglrxavpok.todo.network.UserInfoRepository

class UserInfoViewModel : ViewModel() {
    private val repository = UserInfoRepository()
    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo : LiveData<UserInfo> = _userInfo

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
}