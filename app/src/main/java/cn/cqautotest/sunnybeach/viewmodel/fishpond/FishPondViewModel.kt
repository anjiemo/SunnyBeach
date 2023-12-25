package cn.cqautotest.sunnybeach.viewmodel.fishpond

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.network.FishNetwork
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.paging.source.FishDetailCommendListPagingSource
import cn.cqautotest.sunnybeach.paging.source.FishPagingSource
import cn.cqautotest.sunnybeach.paging.source.UserFishPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/10
 * desc   : 摸鱼列表的 ViewModel
 */
class FishPondViewModel : ViewModel() {

    private val _fishListStateLiveData = MutableLiveData(Unit)
    val fishListStateLiveData = _fishListStateLiveData.switchMap { MutableLiveData(it) }

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

    fun getFishCommendListById(momentId: String): Flow<PagingData<FishPondComment.FishPondCommentItem>> {
        return Pager(
            config = PagingConfig(30),
            pagingSourceFactory = {
                FishDetailCommendListPagingSource(momentId)
            }).flow.cachedIn(viewModelScope)
    }

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

    fun getFishListByCategoryId(topicId: String): Flow<PagingData<Fish.FishItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                FishPagingSource(topicId)
            }).flow.cachedIn(viewModelScope)
    }

    fun getUserFishList(userId: String): Flow<PagingData<Fish.FishItem>> {
        return Pager(
            config = PagingConfig(30),
            pagingSourceFactory = {
                UserFishPagingSource(userId)
            }).flow.cachedIn(viewModelScope)
    }
}