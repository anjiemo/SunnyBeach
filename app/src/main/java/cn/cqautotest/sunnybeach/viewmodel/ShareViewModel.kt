package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.paging.source.UserSharePagingSource

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/12
 * desc   : 分享 ViewModel
 */
class ShareViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val userShareListFlow = Pager(config = PagingConfig(30),
        pagingSourceFactory = {
            val userId = savedStateHandle.get<String>(IntentKey.ID).orEmpty()
            UserSharePagingSource(userId)
        }).flow.cachedIn(viewModelScope)
}