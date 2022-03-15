package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.msg.*
import cn.cqautotest.sunnybeach.paging.source.msg.factory.*
import cn.cqautotest.sunnybeach.paging.source.msg.impl.MsgPagingSourceImpl
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 消息的 ViewModel
 */
class MsgViewModel : ViewModel() {

    fun readQaMsg(msgId: String) = Repository.readQaMsg(msgId)

    fun readAtMeMsg(msgId: String) = Repository.readAtMeMsg(msgId)

    fun readMomentMsg(msgId: String) = Repository.readMomentMsg(msgId)

    fun replyArticleMsg(msgId: String) = Repository.replyArticleMsg(msgId)

    fun readArticleMsg(msgId: String) = Repository.readArticleMsg(msgId)

    fun readAllMsg() = Repository.readAllMsg()

    fun getUnReadMsgCount() = Repository.getUnReadMsgCount()

    fun getArticleMsgList(): Flow<PagingData<ArticleMsg.Content>> {
        return Pager(config = PagingConfig(DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                MsgPagingSourceImpl<ArticleMsg.Content>(ArticleMsgListFactory())
            }).flow.cachedIn(viewModelScope)
    }

    fun getMomentMsgList(): Flow<PagingData<MomentMsg.Content>> {
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                MsgPagingSourceImpl<MomentMsg.Content>(FishMsgListFactory())
            }).flow.cachedIn(viewModelScope)
    }

    fun getQaMsgList(): Flow<PagingData<QaMsg.Content>> {
        return Pager(config = PagingConfig(DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                MsgPagingSourceImpl<QaMsg.Content>(QaMsgListFactory())
            }).flow.cachedIn(viewModelScope)
    }

    fun getLikeMsgList(): Flow<PagingData<LikeMsg.Content>> {
        return Pager(config = PagingConfig(DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                MsgPagingSourceImpl<LikeMsg.Content>(LikeMsgListFactory())
            }).flow.cachedIn(viewModelScope)
    }

    fun getSystemMsgList(): Flow<PagingData<SystemMsg.Content>> {
        return Pager(config = PagingConfig(DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                MsgPagingSourceImpl<SystemMsg.Content>(SystemMsgListFactory())
            }).flow.cachedIn(viewModelScope)
    }

    fun getAtMeMsgList(): Flow<PagingData<AtMeMsg.Content>> {
        return Pager(config = PagingConfig(DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                MsgPagingSourceImpl<AtMeMsg.Content>(AtMeMsgListFactory())
            }).flow.cachedIn(viewModelScope)
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 30
    }
}