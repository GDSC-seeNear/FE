package com.kgg.android.seenear.AdminActivity.MapSearchActivity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.model.LocationLatLngEntity
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.model.SearchResultEntity
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.response.search.Poi
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.response.search.Pois
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.response.search.SearchPoiInfo
import com.kgg.android.seenear.AdminActivity.MapSearchActivity.utility.RetrofitUtil
import com.kgg.android.seenear.R
import com.kgg.android.seenear.databinding.ActivityMapSearchBinding
import com.kgg.android.seenear.databinding.ViewholderSearchResultItemBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


// Google map api를 이용한 위치 검색 및 현위치 받아오기
// 사용 소스 : https://whyprogrammer.tistory.com/625

class SearchActivity : AppCompatActivity(), CoroutineScope {


    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    lateinit var binding: ActivityMapSearchBinding
    lateinit var adapter: SearchRecyclerAdapter

    // 키보드 가릴 때 사용
    lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapSearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        job = Job()

        initAdapter()
        initViews()
        bindViews()
        initData()

    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener {
            searchKeyword(searchBarInputView.text.toString())

            // 키보드 숨기기
            hideKeyboard()
        }

        searchBarInputView.setOnKeyListener { v, keyCode, event ->
            when (keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    searchKeyword(searchBarInputView.text.toString())

                    // 키보드 숨기기
                    hideKeyboard()

                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
    }

    private fun hideKeyboard() {
        if (::inputMethodManager.isInitialized.not()) {
            inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        }
        inputMethodManager.hideSoftInputFromWindow(binding.searchBarInputView.windowToken, 0)
    }

    /*
    `with` scope function 사용
     */
    private fun initViews() = with(binding) {
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter

        // 무한 스크롤 기능 구현
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.adapter ?: return

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val totalItemCount = recyclerView.adapter!!.itemCount - 1

                // 페이지 끝에 도달한 경우
                if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == totalItemCount) {
                    loadNext()
                }
            }
        })
    }

    private fun loadNext() {
        if (binding.recyclerView.adapter?.itemCount == 0)
            return

        searchWithPage(adapter.currentSearchString, adapter.currentPage + 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData(searchInfo: SearchPoiInfo, keywordString: String) {

        val pois: Pois = searchInfo.pois
        // mocking data
        val dataList = pois.poi.map {
            SearchResultEntity(
                name = it.name ?: "빌딩명 없음",
                fullAddress = makeMainAddress(it),
                locationLatLng = LocationLatLngEntity(
                    it.noorLat,
                    it.noorLon
                )
            )
        }
        adapter.setSearchResultList(dataList) {
            Toast.makeText(
                this,
                "빌딩이름 : ${it.name}, 주소 : ${it.fullAddress} 위도/경도 : ${it.locationLatLng}",
                Toast.LENGTH_SHORT
            )
                .show()


        }
        adapter.currentPage = searchInfo.page.toInt()
        adapter.currentSearchString = keywordString
    }

    private fun searchKeyword(keywordString: String) {
        searchWithPage(keywordString, 1)
    }

    private fun searchWithPage(keywordString: String, page: Int) {
        // 비동기 처리
        launch(coroutineContext) {
            try {
                binding.progressCircular.isVisible = true // 로딩 표시
                if (page == 1) {
                    adapter.clearList()
                }
                // IO 스레드 사용
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keywordString,
                        page = page
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        // Main (UI) 스레드 사용
                        withContext(Dispatchers.Main) {
                            Log.e("response LSS", body.toString())
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo, keywordString)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // error 해결 방법
                // Permission denied (missing INTERNET permission?) 인터넷 권한 필요
                // 또는 앱 삭제 후 재설치
            } finally {
                binding.progressCircular.isVisible = false // 로딩 표시 완료
            }
        }
    }

    private fun makeMainAddress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }

    inner class SearchRecyclerAdapter : RecyclerView.Adapter<SearchRecyclerAdapter.SearchResultViewHolder>() {

        private var searchResultList: List<SearchResultEntity> = listOf()
        var currentPage = 1
        var currentSearchString = ""

        private lateinit var searchResultClickListener: (SearchResultEntity) -> Unit

        inner class SearchResultViewHolder(
            private val binding: ViewholderSearchResultItemBinding,
            private val searchResultClickListener: (SearchResultEntity) -> Unit,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bindData(data: SearchResultEntity) = with(binding) {
                titleTextView.text = data.name
                subtitleTextView.text = data.fullAddress

            }

            fun bindViews(data: SearchResultEntity) {
                binding.root.setOnClickListener {
                    searchResultClickListener(data)
                    val intent = Intent()
                    intent.putExtra("address", data.fullAddress)
                    intent.putExtra("latitude", data.locationLatLng.latitude)
                    intent.putExtra("longitude", data.locationLatLng.longitude)
                    setResult(AppCompatActivity.RESULT_OK, intent)
                    finish()
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
            searchResultClickListener: (SearchResultEntity) -> Unit,
        ) {
            this.searchResultList = this.searchResultList + searchResultList
            this.searchResultClickListener = searchResultClickListener
            notifyDataSetChanged()
        }

        fun clearList(){
            searchResultList = listOf()
        }


    }
}

