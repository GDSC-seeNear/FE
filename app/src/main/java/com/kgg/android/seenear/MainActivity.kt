package com.kgg.android.seenear

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kgg.android.seenear.UserActivity.usersignup.UserSignupActivity
import com.kgg.android.seenear.databinding.ActivityMainBinding
import com.kgg.android.seenear.databinding.ActivityUserLoginBinding
import com.kgg.android.seenear.network.RetrofitInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val smsAuthApi by lazy {
        RetrofitInterface.createForImport()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        App.prefs.accessToken?.let { tokenTest(it) }

    }
    // 토큰 테스트
    fun tokenTest(accessToken : String) {

        Log.d("accessToken?",accessToken)

        val AuthorizationHeader = "Bearer " + accessToken

        val callApi = smsAuthApi.tokenTest(authorizationHeader = AuthorizationHeader)
        callApi.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리

                    response.body()?.let{
                        Log.d("request Id :", response.code().toString())
                    }
                } else { // code == 401
                    // 실패 처리
                    response.body()?.let{
                        Log.d("request Id :", response.code().toString())

                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("request Id :", t.message.toString())
            }
        }
        )
    }




}