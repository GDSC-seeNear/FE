package com.kgg.android.seenear.UserActivity.userlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kgg.android.seenear.App
import com.kgg.android.seenear.IntroActivity
import com.kgg.android.seenear.MainActivity
import com.kgg.android.seenear.R
import com.kgg.android.seenear.databinding.ActivityUserLoginBinding
import com.kgg.android.seenear.databinding.ActivityUserSignup2Binding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.loginRequest
import com.kgg.android.seenear.network.data.loginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginBinding
    private val smsAuthApi by lazy {
        RetrofitInterface.createForImport()
    }

    companion object{
        var phoneNum : String ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendBtn.setOnClickListener {
            if (binding.phoneEdittext.text.length == 11){ // 인증번호 전송 완료
                phoneNum = binding.phoneEdittext.text.toString()
                Toast.makeText(this, "휴대폰 번호: "+ phoneNum, Toast.LENGTH_SHORT).show()
                sendSMS(phoneNum = binding.phoneEdittext.text.toString())
            }
            else { // 인증번호 전송 실패
                Toast.makeText(this, "휴대폰 번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.sendText.setOnClickListener {
            if (binding.phoneEdittext.text.length == 11){ // 인증번호 전송 완료
                phoneNum = binding.phoneEdittext.text.toString()
                Toast.makeText(this, "휴대폰 번호: "+ phoneNum, Toast.LENGTH_SHORT).show()
                sendSMS(phoneNum = binding.phoneEdittext.text.toString())
            }
            else { // 인증번호 전송 실패
                Toast.makeText(this, "휴대폰 번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registText.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                logIn(phoneNum.toString(), binding.numberEdittext.text.toString() )
            }
        }

        binding.registerBtn.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                logIn(phoneNum.toString(), binding.numberEdittext.text.toString() )
            }
        }

    }
    // 인증 메세지 전송
    fun sendSMS(phoneNum : String) {

        Log.d("sendSMS?","phoneNum: " + phoneNum)

        val callApi = smsAuthApi.sendSMS(phoneNumber = phoneNum)
        callApi.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    Toast.makeText(this@UserLoginActivity, "인증번호를 전송하였습니다.", Toast.LENGTH_SHORT).show()


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

    // 로그인
    fun logIn(phoneNum : String,certificationNumber : String) {

        Log.d("logIn?","phoneNum: " + phoneNum)
        Log.d("logIn?","certificationNumber: " + certificationNumber)

        val loginRequest = loginRequest(phoneNum,  certificationNumber)
        val callApi = smsAuthApi.logIn(loginRequest)
        callApi.enqueue(object : Callback<loginResponse> {
            override fun onResponse(call: Call<loginResponse>, response: Response<loginResponse>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    Toast.makeText(this@UserLoginActivity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                    response.body()?.let{
                        App.prefs.accessToken= it.accessToken
                        App.prefs.refreshToken= it.refreshToken
                        Log.d("accessToken :", App.prefs.accessToken.toString())
                        Log.d("refreshToken :", App.prefs.refreshToken.toString())

                    }

                    val intent = Intent(this@UserLoginActivity, MainActivity::class.java)
                    startActivity(intent)

                } else { // code == 401
                    // 실패 처리
                    Toast.makeText(this@UserLoginActivity, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("accessToken :", response.code().toString())

                }
            }

            override fun onFailure(call: Call<loginResponse>, t: Throwable) {
                Log.d("accessToken :", t.message.toString())
            }
        }
        )
    }

}