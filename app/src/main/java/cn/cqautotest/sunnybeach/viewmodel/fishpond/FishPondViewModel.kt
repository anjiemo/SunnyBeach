package cn.cqautotest.sunnybeach.viewmodel.fishpond

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.paging.source.FishDetailCommendListPagingSource
import cn.cqautotest.sunnybeach.paging.source.FishPagingSource
import cn.cqautotest.sunnybeach.paging.source.UserFishPagingSource
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/10
 * desc   : 摸鱼列表的 ViewModel
 */
class FishPondViewModel : ViewModel() {

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

    suspend fun uploadFishImage(image: File) = Repository.uploadFishImage(image)

    fun putFish(moment: Map<String, Any?>) = Repository.putFish(moment)

    fun getFishDetailById(momentId: String) = Repository.loadFishDetailById(momentId)

    fun getFishListByCategoryId(topicId: String): Flow<PagingData<Fish.FishItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                FishPagingSource(topicId)
            }).flow.cachedIn(viewModelScope)
    }

    fun getUserFishList(userId: String): Flow<PagingData<Fish.FishItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                UserFishPagingSource(userId)
            }).flow.cachedIn(viewModelScope)
    }
}