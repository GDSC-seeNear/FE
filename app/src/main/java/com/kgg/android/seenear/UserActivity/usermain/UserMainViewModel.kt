package com.kgg.android.seenear.UserActivity.usermain

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgg.android.seenear.network.RetrofitRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserMainViewModel(private val repository : RetrofitRepository) : ViewModel() {

    // 유저 정보 실시간 ui 업데이트를 위한 View Model & Live Observer

    var tokenResponse: MutableLiveData<String> = MutableLiveData()

    fun tokenTest(accessToken: String) {
        viewModelScope.launch {
            Log.d("accessToken?", accessToken)

            val AuthorizationHeader = "Bearer " + accessToken

            tokenResponse = repository.tokenTest(accessToken = AuthorizationHeader)

        }
    }

}