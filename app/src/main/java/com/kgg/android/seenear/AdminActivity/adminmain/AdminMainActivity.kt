package com.kgg.android.seenear.AdminActivity.adminmain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.App
import com.kgg.android.seenear.R
import com.kgg.android.seenear.UserActivity.usermain.UserMainViewModel
import com.kgg.android.seenear.UserActivity.usermain.UserMainViewModelFactory
import com.kgg.android.seenear.databinding.ActivityAdminMainBinding
import com.kgg.android.seenear.databinding.ActivityAdminSignupBinding
import com.kgg.android.seenear.databinding.ActivityMainBinding
import com.kgg.android.seenear.network.RetrofitRepository

class AdminMainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAdminMainBinding
    private lateinit var viewModel : AdminMainViewModel

    // View에 실시간 정보를 담기 위한 MVVM 패턴

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = RetrofitRepository()
        val viewModelFactory = AdminMainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(AdminMainViewModel::class.java)

        App.prefs.accessToken?.let { viewModel.tokenTest(it) }

        viewModel.tokenResponse.observe(this, Observer {
            Log.d("Response",it.toString())
        })
    }



}