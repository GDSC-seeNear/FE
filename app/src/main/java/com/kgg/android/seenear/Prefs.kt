package com.kgg.android.seenear

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE


//Shared Preference
//간단한 설정 값을 앱 내부의 DB에 저장하기 용이한 내부 저장소입니다. (앱 삭제시 데이터도 소거됩니다)

class Prefs(context: Context) { // Preference Class 생성 후 preference 인스턴스를 생성
    private val prefNm="mPref"
    private val prefs=context.getSharedPreferences(prefNm,MODE_PRIVATE)

    var accessToken:String?
        get() = prefs.getString("accessToken",null)
        set(value){
            prefs.edit().putString("accessToken",value).apply()
        }

    var refreshToken:String?
        get() = prefs.getString("refreshToken",null)
        set(value){
            prefs.edit().putString("refreshToken",value).apply()
        }
}

