package com.example.blogsystem.viewmodel.home

import androidx.lifecycle.*
import com.example.blogsystem.model.ArticleInfo
import com.example.blogsystem.model.HomeCategories
import com.example.blogsystem.network.ServiceCreator
import com.example.blogsystem.network.api.HomeApi
import com.example.blogsystem.utils.BLOG_HTTP_OK_CODE
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(), LifecycleObserver {

    private val homeApi by lazy { ServiceCreator.create<HomeApi>() }
    private val _recommendContent = MutableLiveData<ArticleInfo?>()
    val recommendList: LiveData<ArticleInfo?> get() = _recommendContent
    private val _recommendHasNext = MutableLiveData<Boolean>()
    val recommendHasNext: LiveData<Boolean> get() = _recommendHasNext
    private val _homeCategories = MutableLiveData<HomeCategories>()
    val homeCategories: LiveData<HomeCategories> get() = _homeCategories

    /**
     * 加载更多推荐内容，默认加载第一页的内容
     */
    fun loadMoreRecommendContent(firstRequest: Boolean = false) = viewModelScope.launch {
        // 之前请求的数据
        val recommendContent = _recommendContent.value ?: ArticleInfo()
        // 是否有下一页
        var hasNextPage = recommendContent.hasNext
        // 如果是第一次加载
        hasNextPage = firstRequest || hasNextPage
        _recommendHasNext.value = hasNextPage
        // 如果没有下一页则不用往下执行了
        if (hasNextPage.not()) return@launch
        // 当前页码 +1 ，请求下一页的数据
        val nextPage = recommendContent.currentPage + 1
        runCatching {
            homeApi.getRecommendContent(nextPage)
        }.onSuccess {
            val responseData = it.data
            // 如果请求成功，则将之前的数据与刚请求的数据合并
            if (it.code == BLOG_HTTP_OK_CODE) {
                responseData.list.apply {
                    // 如果之前的数据为 null 则直接使用当前请求的数据，
                    // 否则将之前请求的数据添加到当前请求数据的最前面（相当于在之前的数据之后追加数据）
                    addAll(0, recommendContent.list)
                }
                _recommendContent.value = responseData
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun resetRecommendContent() {
        _recommendContent.value = null
    }

    fun getCategories() = viewModelScope.launch {
        runCatching {
            homeApi.getCategories()
        }.onSuccess {
            val homeCategories = it.data
            if (it.code == BLOG_HTTP_OK_CODE) {
                _homeCategories.value = homeCategories
            }
        }
    }
}