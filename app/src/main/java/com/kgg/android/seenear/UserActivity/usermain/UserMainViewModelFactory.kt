package com.kgg.android.seenear.UserActivity.usermain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.network.RetrofitRepository

// 뷰모델에서 파라미터로 Repository를 받아야 하기 때문에 Factory를 생성

class UserMainViewModelFactory(
    private val repository : RetrofitRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserMainViewModel(repository) as T
    }
}