package cn.cqautotest.sunnybeach.viewmodel.home

import androidx.collection.arrayMapOf
import androidx.lifecycle.*
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.HomeApi
import cn.cqautotest.sunnybeach.model.ArticleDetail
import cn.cqautotest.sunnybeach.model.ArticleInfo
import cn.cqautotest.sunnybeach.model.HomeCategories
import cn.cqautotest.sunnybeach.utils.SUNNY_BEACH_HTTP_OK_CODE
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/18
 * desc   : 首页的 ViewModel
 */
class HomeViewModel : ViewModel(), LifecycleObserver {

    private val homeApi by lazy { ServiceCreator.create<HomeApi>() }
    private var currentRecommendPage = 0

    // 文章分类id，当前页码
    private val _currentArticlePageMap by lazy { arrayMapOf<String, Int>() }

    // 缓存所有文章列表的集合
    private val _cacheCategoriesMap = MutableLiveData<Map<String, List<ArticleInfo.ArticleItem>>?>()
    val cacheCategoriesMap: LiveData<Map<String, List<ArticleInfo.ArticleItem>>?> get() = _cacheCategoriesMap

    // 文章集合：文章分类id，文章内容列表
    private val _categoriesMap = MutableLiveData<Map<String, List<ArticleInfo.ArticleItem>>?>()
    val categoriesMap: LiveData<Map<String, List<ArticleInfo.ArticleItem>>?> get() = _categoriesMap

    private val _recommendContent = MutableLiveData<ArticleInfo?>()
    val recommendList: LiveData<ArticleInfo?> get() = _recommendContent
    private val _homeCategories = MutableLiveData<HomeCategories>()
    val homeCategories: LiveData<HomeCategories> get() = _homeCategories

    // 文章详情
    private val _articleDetail = MutableLiveData<ArticleDetail?>()
    val articleDetail: LiveData<ArticleDetail?> get() = _articleDetail

    fun getArticleDetailById(articleId: String) = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            homeApi.getArticleDetailById(articleId = articleId)
        }.onSuccess { response ->
            val responseData = response.data
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _articleDetail.value = responseData
            }
        }
    }

    fun refreshArticleListByCategoryId(categoryId: String) = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            _currentArticlePageMap[categoryId] = 1
            val page = _currentArticlePageMap[categoryId] ?: 1
            homeApi.getArticleListByCategoryId(categoryId = categoryId, page = page)
        }.onSuccess { response ->
            val responseData = response.data
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                // 根据分类id存储分类内容
                val cacheCategoriesMap = (_categoriesMap.value ?: arrayMapOf()).toMutableMap()
                cacheCategoriesMap[categoryId] = responseData.list
                _cacheCategoriesMap.value = cacheCategoriesMap
                _categoriesMap.value = mapOf(categoryId to responseData.list)
                val currentPage = _currentArticlePageMap[categoryId] ?: 1
                _currentArticlePageMap[categoryId] = currentPage + 1
            }
        }
    }

    fun loadMoreArticleListByCategoryId(categoryId: String) = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            val page = _currentArticlePageMap[categoryId] ?: 1
            homeApi.getArticleListByCategoryId(categoryId = categoryId, page = page)
        }.onSuccess { response ->
            val responseData = response.data
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                // 根据分类id存储分类内容
                val cacheCategoriesMap =
                    _categoriesMap.value ?: arrayMapOf<String, List<ArticleInfo.ArticleItem>>()
                (cacheCategoriesMap[categoryId] ?: listOf()).toMutableList().apply {
                    // 将后面请求的文章数据添加到末尾
                    addAll(responseData.list)
                }
                _cacheCategoriesMap.value = cacheCategoriesMap
                _categoriesMap.value = mapOf(categoryId to responseData.list)
                val currentPage = _currentArticlePageMap[categoryId] ?: 1
                _currentArticlePageMap[categoryId] = currentPage + 1
            }
        }
    }

    fun refreshRecommendContent() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            val page = 1
            currentRecommendPage = page
            homeApi.getRecommendContent(page)
        }.onSuccess { response ->
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _recommendContent.value = response.data
                currentRecommendPage++
            }
        }
    }

    /**
     * 加载更多推荐内容，默认加载第一页的内容
     */
    fun loadMoreRecommendContent() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            val page = currentRecommendPage
            homeApi.getRecommendContent(page)
        }.onSuccess { response ->
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _recommendContent.value = response.data
                currentRecommendPage++
            }
        }
    }

    /**
     * 重置推荐内容
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun resetRecommendContent() {
        _recommendContent.value = null
    }

    /**
     * 重置普通文章列表内容
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun resetArticleList() {
        _cacheCategoriesMap.value = null
        _categoriesMap.value = null
    }

    /**
     * 获取文章分类列表
     */
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