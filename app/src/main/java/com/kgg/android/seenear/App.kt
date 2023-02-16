package com.kgg.android.seenear

import android.app.Application

class App : Application(){ // 효율적인 자원관리를 위해 싱글톤 패턴을 사용하고 가장 먼저 실행되기 위해
    // Application을 상속받은 class에서 onCreate 전에 초기화해줍니다.
    companion object{
        lateinit var prefs:Prefs
    }
    override fun onCreate() {
        prefs=Prefs(applicationContext)
        super.onCreate()
    }
}