package com.kgg.android.seenear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kgg.android.seenear.AdminActivity.AdminSigninActivity
import com.kgg.android.seenear.UserActivity.usersignup.UserSignupActivity
import com.kgg.android.seenear.databinding.ActivityIntroBinding
import com.kgg.android.seenear.UserActivity.sttActivity
import com.kgg.android.seenear.UserActivity.userlogin.UserLoginActivity

class IntroActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        //로딩화면
//        val intent = Intent(this, SplashActivity::class.java)
//        startActivity(intent)

        // 사용자 회원가입 바로가기
        binding.userSignupText.setOnClickListener{
            val intent = Intent(this, UserSignupActivity::class.java)
            startActivity(intent)
        }

        // 보호자 회원가입 바로가기
        binding.adminSignupText.setOnClickListener {
            val intent = Intent(this, AdminSigninActivity::class.java)
            startActivity(intent)
        }

        binding.logIn.setOnClickListener {
            val intent = Intent(this, UserLoginActivity::class.java)
            startActivity(intent)
        }


    }

    private var lastTimeBackPressed : Long = 0

    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500) {
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else{
            finishAffinity()
        }
    }
}