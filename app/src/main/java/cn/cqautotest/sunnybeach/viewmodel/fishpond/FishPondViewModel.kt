package cn.cqautotest.sunnybeach.viewmodel.fishpond

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.network.FishNetwork
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.paging.source.FishDetailCommendListPagingSource
import cn.cqautotest.sunnybeach.paging.source.FishPagingSource
import cn.cqautotest.sunnybeach.paging.source.UserFishPagingSource
import cn.cqautotest.sunnybeach.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/10
 * desc   : 摸鱼列表的 ViewModel
 */
class FishPondViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _fishListStateLiveData = MutableLiveData(Unit)
    val fishListStateLiveData = _fishListStateLiveData.switchMap { MutableLiveData(it) }

    val fishCommendListFlow = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = {
            val momentId = savedStateHandle.get<String>(IntentKey.ID).orEmpty()
            FishDetailCommendListPagingSource(momentId)
        }).flow.cachedIn(viewModelScope)

    val fishListFlow = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = {
            val topicId = savedStateHandle.get<String>(IntentKey.ID).orEmpty().ifEmpty { "recommend" }
            FishPagingSource(topicId)
        }).flow.cachedIn(viewModelScope)

    val userFishFlow = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = {
            val userId = savedStateHandle.get<String>(IntentKey.ID).orEmpty()
            UserFishPagingSource(userId)
        }).flow.cachedIn(viewModelScope)

    private val _fishPondTitleBarHeightFlow = MutableStateFlow(0)
    val fishPondTitleBarHeightFlow: StateFlow<Int> get() = _fishPondTitleBarHeightFlow

    fun refreshFishList() {
        _fishListStateLiveData.value = Unit
    }

    fun getFollowedTopicList() = Repository.getFollowedTopicList()

    fun unfollowFishTopic(topicId: String) = Repository.unfollowFishTopic(topicId)

    fun followFishTopic(topicId: String) = Repository.followFishTopic(topicId)

    fun loadTopicList() = Repository.loadTopicList()

    fun dynamicLikes(momentId: String) = Repository.dynamicLikes(momentId)

    fun postComment(momentComment: Map<String, Any?>, isReply: Boolean) =
        Repository.postComment(momentComment, isReply)

    fun putFish(moment: Map<String, Any?>) = Repository.putFish(moment)

    fun getFishDetailById(momentId: String): Flow<Fish.FishItem> {
        return flow {
            val result = FishNetwork.loadFishDetailById(momentId)
            when {
                result.isSuccess() -> emit(result.getData())
                else -> throw ServiceException(result.getMessage())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun updateFishPondTitleBarHeight(height: Int) = viewModelScope.launch {
        _fishPondTitleBarHeightFlow.emit(height)
    }
}