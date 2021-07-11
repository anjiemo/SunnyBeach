package cn.cqautotest.sunnybeach.viewmodel.fishpond

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.FishPondApi
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.FishPondRecommend
import cn.cqautotest.sunnybeach.model.FishPondTopicIndex
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.utils.SUNNY_BEACH_HTTP_OK_CODE
import cn.cqautotest.sunnybeach.utils.TAG
import cn.cqautotest.sunnybeach.utils.logByDebug
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/10
 * desc   : 摸鱼列表的 ViewModel
 */
class FishPondViewModel : ViewModel() {

    private val fishPondApi by lazy { ServiceCreator.create<FishPondApi>() }

    // 全部摸鱼话题列表
    private val _fishPondTopic = MutableLiveData<FishPondTopicList?>()
    val fishFishPondTopicList: LiveData<FishPondTopicList?> get() = _fishPondTopic

    // 摸鱼首页话题列表
    private val _fishPondTopicIndex = MutableLiveData<FishPondTopicIndex?>()
    val fishPondTopicIndex: LiveData<FishPondTopicIndex?> get() = _fishPondTopicIndex

    // 摸鱼
    private val _fishPond = MutableLiveData<Fish?>()
    val fishPond: LiveData<Fish?> get() = _fishPond

    // 评论列表
    private val _fishPondRecommend = MutableLiveData<FishPondRecommend>()
    val fishPondRecommend: LiveData<FishPondRecommend> get() = _fishPondRecommend

    fun loadFishPondRecommendListById(fishPondId: String)  = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            // TODO: 2021/7/11 page后期作为分页参数
            fishPondApi.loadFishPondRecommendListById(fishPondId, 1)
        }.onSuccess {  response ->
            val responseData = response.data
            logByDebug(tag = TAG, msg = "loadFishPondRecommendListById：responseData is ===> $responseData")
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _fishPondRecommend.value = responseData
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun loadFishPondListById(topicId: String) = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            // TODO: 2021/7/11 page后期作为分页参数
            fishPondApi.loadFishPondListById(topicId, 1)
        }.onSuccess { response ->
            val responseData = response.data
            logByDebug(tag = TAG, msg = "loadFishPondListById：responseData is ===> $responseData")
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _fishPond.value = responseData
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun loadTopicListByIndex() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            fishPondApi.loadTopicListByIndex()
        }.onSuccess { response ->
            val responseData = response.data
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _fishPondTopicIndex.value = responseData
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun loadTopicList() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            fishPondApi.loadTopicList()
        }.onSuccess { response ->
            val responseData = response.data
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _fishPondTopic.value = responseData
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun loadHotFish() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            fishPondApi.loadHotFish(10)
        }.onSuccess { response ->
            val responseData = response.data
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _fishPond.value = responseData
            }
        }.onFailure {
            it.printStackTrace()
        }
    }
}