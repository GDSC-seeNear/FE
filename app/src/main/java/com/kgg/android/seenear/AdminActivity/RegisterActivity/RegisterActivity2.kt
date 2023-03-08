package com.kgg.android.seenear.AdminActivity.RegisterActivity

import android.R.attr
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.SearchActivity
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.model.SearchResultEntity
import com.kgg.android.seenear.R
import com.kgg.android.seenear.databinding.ActivityRegister2Binding
import java.util.*


class RegisterActivity2 : AppCompatActivity() {

    // Admin의 User 등록 - 2

    private lateinit var binding: ActivityRegister2Binding
    private lateinit var searchResult: SearchResultEntity
    val REQUEST_CODE = 100

    companion object{
        var fullAddress : String = "주소를 선택해주세요"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegister2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.birthEdittext.setOnClickListener {
            datepicker()
        }

        binding.homeEdittext.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivityForResult(intent,REQUEST_CODE)

        }

        binding.homeEdittext.text = fullAddress

        binding.plusBtnText.setOnClickListener {
            Log.d("visibility check", (binding.familyLayout2.visibility == View.GONE).toString())
            if (binding.familyLayout2.visibility == View.GONE)
                binding.familyLayout2.visibility = View.VISIBLE
            else if (binding.familyLayout3.visibility == View.GONE){
                binding.familyLayout3.visibility = View.VISIBLE
                binding.plusBtn1.visibility = View.GONE
            }
        }

        // 유저 등록하기
        binding.registerBtn.setOnClickListener {
        }


    }

    override fun onResume() {
        super.onResume()





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            Toast.makeText(
                this,
                "주소 : ${data?.getStringExtra("address").toString()}",
                Toast.LENGTH_SHORT
            )
                .show()
            binding.homeEdittext.text = data?.getStringExtra("address").toString()
        }

    }


    fun datepicker(){

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val date_listener =
                DatePickerDialog.OnDateSetListener { p0, year, month, dayOfMonth ->
                    if(month<9&&dayOfMonth<10) {
                        binding.birthEdittext.text = "${year}-0${month + 1}-0${dayOfMonth}"
                    }
                    else if ( month<9&&dayOfMonth>=10) {
                        binding.birthEdittext.text = "${year}-0${month + 1}-${dayOfMonth}"
                    }
                    else if ( month>=9 && dayOfMonth<10) {
                        binding.birthEdittext.text = "${year}-${month + 1}-0${dayOfMonth}"
                    }
                    else {
                        binding.birthEdittext.text = "${year}-${month + 1}-${dayOfMonth}"
                    }
                }
            val builder = DatePickerDialog(this, date_listener, year, month, day).apply {
                datePicker.maxDate = System.currentTimeMillis()}
            builder.show()


    }

}