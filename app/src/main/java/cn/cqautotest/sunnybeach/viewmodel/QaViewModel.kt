package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.other.QaType
import cn.cqautotest.sunnybeach.paging.source.QaPagingSource
import cn.cqautotest.sunnybeach.paging.source.UserQaPagingSource

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 问答 ViewModel
 */
class QaViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val qaListFlow = Pager(config = PagingConfig(30),
        pagingSourceFactory = {
            val qaType = QaType.LATEST
            QaPagingSource(qaType)
        }).flow.cachedIn(viewModelScope)

    val userQaListFlow = Pager(config = PagingConfig(30),
        pagingSourceFactory = {
            val userId = savedStateHandle.get<String>(IntentKey.ID).orEmpty()
            UserQaPagingSource(userId)
        }).flow.cachedIn(viewModelScope)
}