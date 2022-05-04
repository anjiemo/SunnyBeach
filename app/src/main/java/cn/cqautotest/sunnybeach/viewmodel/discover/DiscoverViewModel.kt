package cn.cqautotest.sunnybeach.viewmodel.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.photo.PhotoApi
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBannerBean
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 发现界面的 ViewModel
 */
class DiscoverViewModel : ViewModel() {

    private val mPhotoApi by lazy { ServiceCreator.create<PhotoApi>() }

    // 当前请求的图片页码
    private val _currentPhotoPage = MutableLiveData<Int>()

    // 轮播图集合
    private val _bannerList = MutableLiveData<List<WallpaperBannerBean.Data>>()
    val wallpaperBannerList: LiveData<List<WallpaperBannerBean.Data>> get() = _bannerList

    // 缓存的所有图片集合
    private val _cacheVerticalPhotoList = MutableLiveData<List<WallpaperBean.Res.Vertical>>()
    val cacheVerticalPhotoList: LiveData<List<WallpaperBean.Res.Vertical>> get() = _cacheVerticalPhotoList

    // 当前加载更多的集合
    private val _verticalPhotoList = MutableLiveData<List<WallpaperBean.Res.Vertical>>()
    val verticalPhotoList: LiveData<List<WallpaperBean.Res.Vertical>> get() = _verticalPhotoList

    /**
     * 刷新图片列表数据
     */
    fun refreshPhotoList() {
        viewModelScope.launch {
            // 重置当前页为 0
            _currentPhotoPage.value = 0
            // 默认当前为第 0 页
            val currentPage = _currentPhotoPage.value ?: 0
            // 查询 10 条数据
            val limit = 30
            // 设置本次请求需要跳过几张图片
            val skip = currentPage * 10
            Timber.d("currentPage：$currentPage skip：$skip")
            runCatching {
                // 查询多少条数据，跳过多少条数据
                withContext(Dispatchers.IO) {
                    mPhotoApi.loadWallpaperList(limit = limit, skip = skip)
                }
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
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    /**
     * 加载更多图片列表数据
     */
    fun loadMorePhotoList(limit: Int = 10) {
        viewModelScope.launch {
            // 默认当前为第 0 页
            val currentPage = _currentPhotoPage.value ?: 0
            // 设置本次请求需要跳过几张图片
            val skip = currentPage * 10
            Timber.d("currentPage：$currentPage skip：$skip")
            runCatching {
                // 查询多少条数据，跳过多少条数据
                withContext(Dispatchers.IO) {
                    mPhotoApi.loadWallpaperList(limit = limit, skip = skip)
                }
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
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    // fun loadBannerList() {
    //     viewModelScope.launch {
    //         runCatching {
    //             withContext(Dispatchers.IO) {
    //                 mPhotoApi.loadWallpaperBannerList()
    //             }
    //         }.onSuccess { response ->
    //             val responseData = response.data
    //             if ("0" == response.errno) {
    //                 _bannerList.value = responseData
    //             }
    //         }.onFailure {
    //             it.printStackTrace()
    //         }
    //     }
    // }
}