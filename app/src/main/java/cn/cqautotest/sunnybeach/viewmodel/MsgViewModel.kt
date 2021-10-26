package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.msg.*
import cn.cqautotest.sunnybeach.paging.source.msg.*
import cn.cqautotest.sunnybeach.viewmodel.app.Repository
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 消息的 ViewModel
 */
class MsgViewModel : ViewModel() {

    fun readAllMsg() = Repository.readAllMsg()

    fun getArticleMsgList(): Flow<PagingData<ArticleMsg.Content>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                ArticleMsgPagingSource()
            }).flow.cachedIn(viewModelScope)
    }

    fun getMomentMsgList(): Flow<PagingData<MomentMsg.Content>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                MomentMsgPagingSource()
            }).flow.cachedIn(viewModelScope)
    }

    fun getQAMsgList(): Flow<PagingData<QAMsg.Content>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                QAMsgPagingSource()
            }).flow.cachedIn(viewModelScope)
    }

    fun getLikeMsgList(): Flow<PagingData<LikeMsg.Content>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                LikeMsgPagingSource()
            }).flow.cachedIn(viewModelScope)
    }

    fun getSystemMsgList(): Flow<PagingData<SystemMsg.Content>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                SystemMsgPagingSource()
            }).flow.cachedIn(viewModelScope)
    }
}