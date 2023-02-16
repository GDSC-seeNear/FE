package com.kgg.android.seenear.UserActivity.usersignup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.kgg.android.seenear.R
import com.kgg.android.seenear.databinding.ActivityUserSignupBinding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.checkValRequest
import com.kgg.android.seenear.network.data.checkValResponse
import com.kgg.android.seenear.network.data.signupResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class UserSignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserSignupBinding

    private val smsAuthApi by lazy {
        RetrofitInterface.createForImport()
    }

    companion object{
        var phoneNum : String ?= null
        var signUpToken : String ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {

//        viewModel = ViewModelProviders.of(this).get(UserSignupViewModel::class.java)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = this

        super.onCreate(savedInstanceState)
        binding = ActivityUserSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


        binding.registerBtn.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                checkVal(phoneNum!!, binding.numberEdittext.text.toString())
            }
        }

        binding.registText.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                checkVal(phoneNum!!, binding.numberEdittext.text.toString())
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
                    Toast.makeText(this@UserSignupActivity, "인증번호를 전송하였습니다.", Toast.LENGTH_SHORT).show()

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

    // 코드 유효성 검사
    fun checkVal(phoneNumber : String, certificationNumber : String) {

        Log.d("checkVal?","phoneNumber: " + phoneNumber)
        Log.d("checkVal?","certificationNumber: " + certificationNumber)


        val checkValRequest = checkValRequest(phoneNumber = phoneNumber, certificationNumber = certificationNumber)
        val callApi = smsAuthApi.checkVal(checkValRequest)
        callApi.enqueue(object : Callback<checkValResponse> {
            override fun onResponse(call: Call<checkValResponse>, response: Response<checkValResponse>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    response.body()?.let{
                        Log.d("signUpToken :", it.signUpToken.toString())
                        signUpToken = it.signUpToken.toString()
                    }
                    val intent = Intent(this@UserSignupActivity, UserSignupActivity2::class.java)
                    intent.putExtra("phoneNum", phoneNum)
                    startActivity(intent)
                } else { // code == 401
                    // 실패 처리
                    Log.d("onFailure :", "else")

                }
            }
            override fun onFailure(call: Call<checkValResponse>, t: Throwable) {
                Log.d("onFailure :", "onFailure")

            }
        }
        )
    }




}