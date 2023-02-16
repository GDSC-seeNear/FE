package com.kgg.android.seenear.AdminActivity.MapSearchActivity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.MapActivity.Companion.CAMERA_ZOOM_LEVEL
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.MapActivity.Companion.PERMISSION_REQUEST_CODE
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.model.LocationLatLngEntity
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.model.SearchResultEntity
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.utility.RetrofitUtil
import com.kgg.android.seenear.R
import com.kgg.android.seenear.databinding.ActivityMapBinding
import com.kgg.android.seenear.databinding.ViewholderSearchResultItemBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap
    private var currentSelectMarker: Marker? = null

    private lateinit var searchResult: SearchResultEntity

    private lateinit var locationManager: LocationManager // 안드로이드 에서 위치정보 불러올 때 관리해주는 유틸 클래스

    private lateinit var myLocationListener: MyLocationListener // 나의 위치를 불러올 리스너

    companion object {
        const val SEARCH_RESULT_EXTRA_KEY: String = "SEARCH_RESULT_EXTRA_KEY"
        const val CAMERA_ZOOM_LEVEL = 17f
        const val PERMISSION_REQUEST_CODE = 2021
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        if (::searchResult.isInitialized.not()) {
            intent?.let {
                searchResult = it.getParcelableExtra<SearchResultEntity>(SEARCH_RESULT_EXTRA_KEY)
                    ?: throw Exception("데이터가 존재하지 않습니다.")
                setupGoogleMap()
            }
        }

        bindViews()





    }

    private fun bindViews() = with(binding) {
        // 현재 위치 버튼 리스너
        currentLocationButton.setOnClickListener {
            binding.progressCircular.isVisible = true
            getMyLocation()
        }
    }


    private fun setupGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(binding.mapFragment.id) as SupportMapFragment
        mapFragment.getMapAsync(this) // callback 구현 (onMapReady)

        // 마커 데이터 보여주기

    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map


        currentSelectMarker = setupMarker(searchResult)

        currentSelectMarker?.showInfoWindow()


        //마커 클릭 리스너-마커 클릭하면 카드뷰 띄움
        this.map!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                Toast.makeText(this@MapActivity, "클릭!!", Toast.LENGTH_SHORT).show()

                return false
            }
        })

    }

    private fun setupMarker(searchResult: SearchResultEntity): Marker {

        // 구글맵 전용 위도/경도 객체
        val positionLatLng = LatLng(
            searchResult.locationLatLng.latitude.toDouble(),
            searchResult.locationLatLng.longitude.toDouble()
        )

        // 구글맵 마커 객체 설정
        val markerOptions = MarkerOptions().apply {
            position(positionLatLng)
            title(searchResult.name)
            snippet(searchResult.fullAddress)
        }

        // 카메라 줌 설정
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, CAMERA_ZOOM_LEVEL))

        return map.addMarker(markerOptions)
    }

    private fun getMyLocation() {
        // 위치 매니저 초기화
        if (::locationManager.isInitialized.not()) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        // GPS 이용 가능한지
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // 권한 얻기
        if (isGpsEnable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when {
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) -> {
                        showPermissionContextPop()
                    }

                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED -> {
                        makeRequestAsync()
                    }

                    else -> {
                        setMyLocationListener()
                    }
                }
            }
        }
    }

    private fun showPermissionContextPop() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("내 위치를 불러오기위해 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                makeRequestAsync()
            }
            .create()
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationListener() {
        val minTime = 3000L // 현재 위치를 불러오는데 기다릴 최소 시간
        val minDistance = 100f // 최소 거리 허용

        // 로케이션 리스너 초기화
        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }

        // 현재 위치 업데이트 요청
        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                myLocationListener
            )
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime,
                minDistance,
                myLocationListener
            )
        }
    }

    private fun onCurrentLocationChanged(locationLatLngEntity: LocationLatLngEntity) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    locationLatLngEntity.latitude.toDouble(),
                    locationLatLngEntity.longitude.toDouble()
                ), CAMERA_ZOOM_LEVEL
            )
        )

        loadReverseGeoInformation(locationLatLngEntity)
        removeLocationListener() // 위치 불러온 경우 더이상 리스너가 필요 없으므로 제거
    }

    private fun loadReverseGeoInformation(locationLatLngEntity: LocationLatLngEntity) {
        // 코루틴 사용
        launch(coroutineContext) {
            try {
                binding.progressCircular.isVisible = true

                // IO 스레드에서 위치 정보를 받아옴
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getReverseGeoCode(
                        lat = locationLatLngEntity.latitude.toDouble(),
                        lon = locationLatLngEntity.longitude.toDouble()
                    )
                    if (response.isSuccessful) {
                        val body = response.body()

                        // 응답 성공한 경우 UI 스레드에서 처리
                        withContext(Dispatchers.Main) {
                            Log.e("list", body.toString())
                            body?.let {
                                currentSelectMarker = setupMarker(
                                    SearchResultEntity(
                                        fullAddress = it.addressInfo.fullAddress ?: "주소 정보 없음",
                                        name = "내 위치",
                                        locationLatLng = locationLatLngEntity
                                    )
                                )
                                // 마커 보여주기
                                currentSelectMarker?.showInfoWindow()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MapActivity, "검색하는 과정에서 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressCircular.isVisible = false
            }
        }
    }

    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener) // myLocationListener 를 업데이트 대상에서 지워줌
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setMyLocationListener()
                } else {
                    Toast.makeText(this, "권한을 받지 못했습니다.", Toast.LENGTH_SHORT).show()
                    binding.progressCircular.isVisible = false
                }
            }
        }
    }

    private fun makeRequestAsync() {
        // 퍼미션 요청 작업. 아래 작업은 비동기로 이루어짐
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            // 현재 위치 콜백
            val locationLatLngEntity = LocationLatLngEntity(
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )

            onCurrentLocationChanged(locationLatLngEntity)
        }

    }

    inner class SearchRecyclerAdapter : RecyclerView.Adapter<SearchRecyclerAdapter.SearchResultViewHolder>() {

        private var searchResultList: List<SearchResultEntity> = listOf()
        var currentPage = 1
        var currentSearchString = ""

        private lateinit var searchResultClickListener: (SearchResultEntity) -> Unit

        inner class SearchResultViewHolder(
            private val binding: ViewholderSearchResultItemBinding,
            private val searchResultClickListener: (SearchResultEntity) -> Unit
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bindData(data: SearchResultEntity) = with(binding) {
                titleTextView.text = data.name
                subtitleTextView.text = data.fullAddress
                Log.d("check location : " , data.locationLatLng.toString())
                val intent = Intent()
                intent.putExtra("address", data.fullAddress)
                intent.putExtra("latitude", data.locationLatLng.latitude)
                intent.putExtra("longitude", data.locationLatLng.longitude)
                setResult(AppCompatActivity.RESULT_OK, intent)
                finish()
            }

            fun bindViews(data: SearchResultEntity) {
                binding.root.setOnClickListener {
                    Log.d("check location : " , data.locationLatLng.toString())
                    searchResultClickListener(data)

                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
            val binding = ViewholderSearchResultItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return SearchResultViewHolder(binding, searchResultClickListener)
        }

        override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
            holder.bindData(searchResultList[position])
            holder.bindViews(searchResultList[position])
        }

        override fun getItemCount(): Int {
            return searchResultList.size
        }

        @SuppressLint("NotifyDataSetChanged")
        //  해당 프로젝트의 설정 된 minSdkVersion 이후에 나온 API를 사용할때  warning을 없애고 개발자가 해당 APi를 사용할 수 있게 함
        fun setSearchResultList(
            searchResultList: List<SearchResultEntity>,
            searchResultClickListener: (SearchResultEntity) -> Unit
        ) {
            this.searchResultList = this.searchResultList + searchResultList
            this.searchResultClickListener = searchResultClickListener
            notifyDataSetChanged()
        }

        fun clearList(){
            searchResultList = listOf()
        }


    }


    override fun onMarkerClick(p0: Marker?): Boolean {
        Log.d("checkmarker", "click!!")
        return false
    }
}