package com.kgg.android.seenear.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.kgg.android.seenear.network.data.AuthorizationHeader
import com.kgg.android.seenear.network.data.loginResponse
import com.kgg.android.seenear.network.data.signupResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory // data Json을 자바에서 사용 가능한 gson으로 바꿔줌

// Retrofit 객체 제작 !!
// API를 부를 때 사용해야 하는 첫번째 클래스, 앞서 설치한 Retrofit의 클래스를 생성하고 api 클래스를 Retrofit.class에 연결시켜주는 역할
// 서버 호출이 필요할 때마다 인터페이스를 구현해야 한다면 너무 비효율적이기 때문에 Client 파일은 싱글톤으로 따로 Retrofit 객체를 제작하는 것이 바람직하다

//MVVM 패턴을 위해 데이터 통신을 하는 Repository 를 생성해준다.
//여기서 통신한 값을 뷰모델에서 사용할 것이다
//

class RetrofitRepository {

    // 토큰 테스트
    fun tokenTest(accessToken : String): MutableLiveData<String> {

        val tokenResponse: MutableLiveData<String> = MutableLiveData()

        val callApi = RetrofitInterface.RetrofitInstance.api.tokenTest(accessToken)
        callApi.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful()) { // <--> response.code == 200
                    // 성공 처리
                    response.body()?.let {
                        Log.d("request Id in success :", response.code().toString())
                        tokenResponse.value = it
                    }
                } else { // code == 401
                    // 실패 처리
                    response.body()?.let {
                        Log.d("request Id in not :", response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("request Id in failure :", t.message.toString())
            }
        })

        return tokenResponse
    }

}
//    // 인증 메세지 전송
//    suspend fun sendSMS(phoneNum : String) {
//
//        val callGetStudent = RetrofitInterface.RetrofitInstance.api.sendSMS(phoneNumber = phoneNum)
//        callGetStudent.enqueue(object : Callback<String> {
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                if (response.isSuccessful()) { // <--> response.code == 200
//                    // 성공 처리
//
//                } else { // code == 400
//                    // 실패 처리
//                }
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//
//            }
//        }
//        )
//    }
//
//    // 코드 유효성 검사
//    fun checkVal(requestId : String, certificationNumber : String) {
//
//        val callGetStudent = RetrofitInterface.createForImport().checkVal(requestId = requestId, certificationNumber = certificationNumber)
//        callGetStudent.enqueue(object : Callback<Boolean> {
//            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
//                if (response.isSuccessful()) { // <--> response.code == 200
//                    // 성공 처리
//                } else { // code == 400
//                    // 실패 처리
//                }
//            }
//            override fun onFailure(call: Call<Boolean>, t: Throwable) {
//
//            }
//        }
//        )
//    }
//
////     회원가입
//    fun signUp(name : String, phoneNum : String) {
//
//        val callGetStudent = RetrofitInterface.createForImport().signUp(name, phoneNum)
//        callGetStudent.enqueue(object : Callback<signupResponse> {
//            override fun onResponse(call: Call<signupResponse>, response: Response<signupResponse>) {
//                if (response.isSuccessful()) { // <--> response.code == 200
//                    // 성공 처리
//                } else { // code == 400
//                    // 실패 처리
//                }
//            }
//            override fun onFailure(call: Call<signupResponse>, t: Throwable) {
//
//            }
//        }
//        )
//    }
//
//     로그인
//    fun logIn(phoneNumber : String, requestId : String, certificationNumber : String) {
//
//        val callGetStudent = RetrofitInterface.createForImport().logIn(phoneNumber, requestId, certificationNumber)
//        callGetStudent.enqueue(object : Callback<loginResponse> {
//            override fun onResponse(call: Call<loginResponse>, response: Response<loginResponse>) {
//                if (response.isSuccessful()) { // <--> response.code == 200
//                    // 성공 처리
//                } else { // code == 400
//                    // 실패 처리
//                }
//            }
//            override fun onFailure(call: Call<loginResponse>, t: Throwable) {
//
//            }
//        }
//        )
//    }


