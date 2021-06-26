package cn.cqautotest.sunnybeach.viewmodel.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.PhotoApi
import cn.cqautotest.sunnybeach.http.response.model.HomeBannerBean
import cn.cqautotest.sunnybeach.http.response.model.HomePhotoBean
import cn.cqautotest.sunnybeach.utils.logByDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoverViewModel : ViewModel() {

    private val mPhotoApi by lazy { ServiceCreator.create<PhotoApi>() }

    // 当前请求的图片页码
    private val _currentPhotoPage = MutableLiveData<Int>()

    // 轮播图集合
    private val _bannerList = MutableLiveData<List<HomeBannerBean.Data>>()
    val bannerList: LiveData<List<HomeBannerBean.Data>> get() = _bannerList

    // 缓存的所有图片集合
    private val _cacheVerticalPhotoList = MutableLiveData<List<HomePhotoBean.Res.Vertical>>()
    val cacheVerticalPhotoList: LiveData<List<HomePhotoBean.Res.Vertical>> get() = _cacheVerticalPhotoList

    // 当前加载更多的集合
    private val _verticalPhotoList = MutableLiveData<List<HomePhotoBean.Res.Vertical>>()
    val verticalPhotoList: LiveData<List<HomePhotoBean.Res.Vertical>> get() = _verticalPhotoList

    /**
     * 刷新图片列表数据
     */
    fun refreshPhotoList() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        // 重置当前页为 0
        _currentPhotoPage.value = 0
        // 默认当前为第 0 页
        val currentPage = _currentPhotoPage.value ?: 0
        // 查询 10 条数据
        val limit = 10
        // 设置本次请求需要跳过几张图片
        val skip = currentPage * 10
        logByDebug(msg = "===> loadMorePhotoList： currentPage：$currentPage skip：$skip")
        runCatching {
            // 查询多少条数据，跳过多少条数据
            mPhotoApi.loadPhotoList(limit = limit, skip = skip)
        }.onSuccess { response ->
            val responseData = response.res.vertical
            if (0 == response.code) {
                val cachePhotoList = (_cacheVerticalPhotoList.value ?: listOf()).toMutableList()
                cachePhotoList.addAll(responseData)
                // 设置缓存的全部图片集合
                _cacheVerticalPhotoList.value = cachePhotoList
                // 本次加载更多的图片集合
                _verticalPhotoList.value = responseData
                // 本次查询成功，当前页码 +1
                _currentPhotoPage.value = currentPage + 1
            }
        }
    }

    /**
     * 加载更多图片列表数据
     */
    fun loadMorePhotoList(limit: Int = 10) = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        // 默认当前为第 0 页
        val currentPage = _currentPhotoPage.value ?: 0
        // 设置本次请求需要跳过几张图片
        val skip = currentPage * 10
        logByDebug(msg = "===> loadMorePhotoList： currentPage：$currentPage skip：$skip")
        runCatching {
            // 查询多少条数据，跳过多少条数据
            mPhotoApi.loadPhotoList(limit = limit, skip = skip)
        }.onSuccess { response ->
            val responseData = response.res.vertical
            if (0 == response.code) {
                val cachePhotoList = (_cacheVerticalPhotoList.value ?: listOf()).toMutableList()
                cachePhotoList.addAll(responseData)
                // 设置缓存的全部图片集合
                _cacheVerticalPhotoList.value = cachePhotoList
                // 本次加载更多的图片集合
                _verticalPhotoList.value = responseData
                // 本次查询成功，当前页码 +1
                _currentPhotoPage.value = currentPage + 1
            }
        }
    }

    fun loadBannerList() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            mPhotoApi.listsHomeBannerList()
        }.onSuccess { response ->
            val responseData = response.data
            if ("0" == response.errno) {
                _bannerList.value = responseData
            }
        }
    }
}