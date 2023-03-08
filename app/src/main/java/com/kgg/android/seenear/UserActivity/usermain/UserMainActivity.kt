package com.kgg.android.seenear.UserActivity.usermain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kgg.android.seenear.App
import com.kgg.android.seenear.databinding.ActivityMainBinding
import com.kgg.android.seenear.network.RetrofitInterface
import com.kgg.android.seenear.network.RetrofitRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel : UserMainViewModel

    // View에 실시간 정보를 담기 위한 MVVM 패턴

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = RetrofitRepository()
        val viewModelFactory = UserMainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(UserMainViewModel::class.java)

        App.prefs.accessToken?.let { viewModel.tokenTest(it) }

        viewModel.tokenResponse.observe(this, Observer {
            Log.d("Response",it.toString())
            binding.userNameText.text = it.toString()
        })
    }



}