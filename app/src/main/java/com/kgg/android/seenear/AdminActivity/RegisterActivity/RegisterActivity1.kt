package com.kgg.android.seenear.AdminActivity.RegisterActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kgg.android.seenear.R
import com.kgg.android.seenear.UserActivity.usersignup.UserSignupActivity
import com.kgg.android.seenear.UserActivity.usersignup.UserSignupActivity2
import com.kgg.android.seenear.databinding.ActivityRegister1Binding
import com.kgg.android.seenear.databinding.ActivityUserSignupBinding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.data.checkValRequest
import com.kgg.android.seenear.network.data.checkValResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity1 : AppCompatActivity() {

    // Admin의 User 등록 - 1

    private lateinit var binding: ActivityRegister1Binding


    private val smsAuthApi by lazy {
        RetrofitInterface.RetrofitInstance.api
    }

    companion object{
        var phoneNum : String ?= null
        var signUpToken : String ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister1Binding.inflate(layoutInflater)
        setContentView(binding.root)


        // bottom sheet 설정
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)
        val userCheckBtn = bottomSheetDialog.findViewById<ImageView>(R.id.userCheckBtn)
        val userInfo_text = bottomSheetDialog.findViewById<EditText>(R.id.userInfo_text)

        // 인증 메세지 전송
        fun sendSMS(phoneNum : String) {

            Log.d("sendSMS?","phoneNum: " + phoneNum)

            val callApi = smsAuthApi.sendSMS(phoneNumber = phoneNum)
            callApi.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful()) { // <--> response.code == 200
                        // 성공 처리
                        Toast.makeText(this@RegisterActivity1, "인증번호를 전송하였습니다.", Toast.LENGTH_SHORT).show()

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
                            UserSignupActivity.signUpToken = it.signUpToken.toString()
                        }

                        bottomSheetDialog.show()

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

        binding.sendText.setOnClickListener {
            if (binding.phoneEdittext.text.length == 11){ // 인증번호 전송 완료
                UserSignupActivity.phoneNum = binding.phoneEdittext.text.toString()
                Toast.makeText(this, "휴대폰 번호: "+ UserSignupActivity.phoneNum, Toast.LENGTH_SHORT).show()
                sendSMS(phoneNum = binding.phoneEdittext.text.toString())
            }
            else { // 인증번호 전송 실패
                Toast.makeText(this, "휴대폰 번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.sendBtn.setOnClickListener {
            if (binding.phoneEdittext.text.length == 11){ // 인증번호 전송 완료
                UserSignupActivity.phoneNum = binding.phoneEdittext.text.toString()
                Toast.makeText(this, "휴대폰 번호: "+ UserSignupActivity.phoneNum, Toast.LENGTH_SHORT).show()
                sendSMS(phoneNum = binding.phoneEdittext.text.toString())
            }
            else { // 인증번호 전송 실패
                Toast.makeText(this, "휴대폰 번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.registerBtn.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                checkVal(UserSignupActivity.phoneNum!!, binding.numberEdittext.text.toString())
            }
        }

        binding.registText.setOnClickListener {
            if (binding.numberEdittext.text.length ==4){
                checkVal(UserSignupActivity.phoneNum!!, binding.numberEdittext.text.toString())
            }
        }


        userCheckBtn?.setOnClickListener {
            val intent = Intent(this, RegisterActivity2::class.java)
            intent.putExtra("phoneNum", phoneNum)
            startActivity(intent)
        }


    }



}