package com.kgg.android.seenear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Loadingstart()
    }

    private fun Loadingstart() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            val intent = Intent(applicationContext, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}