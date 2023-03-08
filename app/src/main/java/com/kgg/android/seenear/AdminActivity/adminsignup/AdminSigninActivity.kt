package com.kgg.android.seenear.AdminActivity.adminsignup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kgg.android.seenear.R
import com.kgg.android.seenear.databinding.ActivityAdminSignupBinding

class AdminSigninActivity : AppCompatActivity() {

    private var binding : ActivityAdminSignupBinding?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_signup)

        binding = ActivityAdminSignupBinding.inflate(layoutInflater)

    }
}