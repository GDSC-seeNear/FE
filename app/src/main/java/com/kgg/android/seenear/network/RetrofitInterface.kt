package com.kgg.android.seenear.network

import com.google.gson.GsonBuilder
import com.kgg.android.seenear.network.Constants.Companion.BASE_URL
import com.kgg.android.seenear.network.data.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

interface RetrofitInterface {

    object RetrofitInstance {

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .addInterceptor { chain -> chain.proceed(chain.request()) }
            .addInterceptor(interceptor)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        private val retrofit by lazy {
            Retrofit.Builder()
//                .client(client)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        val api : RetrofitInterface by lazy {
            retrofit.create(RetrofitInterface::class.java)
        }
    }


    //sms 발송
    @POST("auth/send")
    fun sendSMS(@Body phoneNumber: String) : Call<String> // body X

    //코드 유효성 검사
    @POST("auth/check")
    fun checkVal(@Body checkValRequest: checkValRequest) : Call<checkValResponse> // signUpToken

    //회원가입
    @POST("auth/signup")
    fun signUp(
        @Header ("Authorization") authorizationHeader: String,
        @Body signupRequest: signupRequest) : Call<signupResponse>

    //로그인
    @POST("auth/login")
    fun logIn(@Body loginRequest: loginRequest) : Call<loginResponse>

    //토큰 테스트
    @GET("auth/c")
    fun tokenTest(
        @Header ("Authorization") authorizationHeader: String,
        ) : Call<String> // 해당 토큰 주인의 name


}
