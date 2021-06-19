package com.example.blogsystem.viewmodel.home

import androidx.collection.arrayMapOf
import androidx.lifecycle.*
import com.blankj.utilcode.util.NetworkUtils
import com.example.blogsystem.model.ArticleInfo
import com.example.blogsystem.model.HomeCategories
import com.example.blogsystem.network.ServiceCreator
import com.example.blogsystem.network.api.HomeApi
import com.example.blogsystem.utils.SUNNY_BEACH_HTTP_OK_CODE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel(), LifecycleObserver {

    private val homeApi by lazy { ServiceCreator.create<HomeApi>() }

    // 文章分类id，文章内容列表
    private val _categoriesMap by lazy { arrayMapOf<String, List<ArticleInfo.ArticleItem>>() }
    private val _recommendContent = MutableLiveData<ArticleInfo?>()
    val recommendList: LiveData<ArticleInfo?> get() = _recommendContent
    private val _homeCategories = MutableLiveData<HomeCategories>()
    val homeCategories: LiveData<HomeCategories> get() = _homeCategories

    fun getCommendContentByCategoryId(categoryId: String, page: Int = 1) = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            homeApi.getCommendContentByCategoryId(categoryId = categoryId, page = page)
        }.onSuccess { response ->
            val responseData = response.data
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                // TODO: 2021/6/19 根据分类id存储分类内容
                _categoriesMap[categoryId] = responseData.list
            }
        }
    }

    /**
     * 加载更多推荐内容，默认加载第一页的内容
     */
    fun loadMoreRecommendContent(page: Int) = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            homeApi.getRecommendContent(page)
        }.onSuccess { response ->
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _recommendContent.value = response.data
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun resetRecommendContent() {
        _recommendContent.value = null
    }

    fun getCategories() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            homeApi.getCategories()
        }.onSuccess {
            val homeCategories = it.data
            if (SUNNY_BEACH_HTTP_OK_CODE == it.code) {
                _homeCategories.value = homeCategories
            }
        }
    }
}