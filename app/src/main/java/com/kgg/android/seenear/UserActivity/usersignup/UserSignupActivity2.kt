package com.kgg.android.seenear.UserActivity.usersignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.kgg.android.seenear.IntroActivity
import com.kgg.android.seenear.R
import com.kgg.android.seenear.databinding.ActivityUserSignup2Binding
import com.kgg.android.seenear.databinding.ActivityUserSignupBinding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.AuthorizationHeader
import com.kgg.android.seenear.network.data.loginRequest
import com.kgg.android.seenear.network.data.signupRequest
import com.kgg.android.seenear.network.data.signupResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Header

class UserSignupActivity2 : AppCompatActivity(){

    private lateinit var binding: ActivityUserSignup2Binding
    private var phoneNum: String ?= null
    private val smsAuthApi by lazy {
        RetrofitInterface.RetrofitInstance.api
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSignup2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        phoneNum = intent.getStringExtra("phoneNum")

        binding.registerBtn.setOnClickListener {
            if (binding.nameEdittext.text.length >= 2){
                signUp(binding.nameEdittext.text.toString(), phoneNum!!)
            }
            else
                Toast.makeText(this, "이름을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show()
        }

        binding.registText.setOnClickListener {
            if (binding.nameEdittext.text.length >= 2){
                signUp(binding.nameEdittext.text.toString(), phoneNum!!)
            }
            else
                Toast.makeText(this, "이름을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 회원가입
    fun signUp(name : String, phoneNum : String) {

        Log.d("signup?","name: " + name)
        Log.d("signup?","phoneNum: " + phoneNum)
        Log.d("signup?","signUpToken: " + UserSignupActivity.signUpToken)

        val AuthorizationHeader = "Bearer " + UserSignupActivity.signUpToken
        val signupRequest = signupRequest(name, phoneNum)

        val callApi = smsAuthApi.signUp(AuthorizationHeader, signupRequest)
        callApi.enqueue(object : Callback<signupResponse> {
            override fun onResponse(call: Call<signupResponse>, response: Response<signupResponse>) {
                if (response.isSuccessful()) { // <--> response.code == 200

                    Toast.makeText(this@UserSignupActivity2, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@UserSignupActivity2, IntroActivity::class.java)
                    startActivity(intent)

                    response.body()?.let{
                        Log.d("signup?", "accesstoken: " + it.accessToken.toString())
                        Log.d("signup?", "refreshtoken: " + it.refreshToken.toString())

                    }

                    // 성공 처리
                } else {

                    var stringToJson = JSONObject(response.errorBody()?.string()!!)
                    Log.e("signup?", "stringToJson: ${stringToJson.getString("message")}")

                    Toast.makeText(this@UserSignupActivity2, stringToJson.getString("message"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@UserSignupActivity2, IntroActivity::class.java)
                    startActivity(intent)
                }
            }
            override fun onFailure(call: Call<signupResponse>, t: Throwable) {

            }
        }
        )
    }

}