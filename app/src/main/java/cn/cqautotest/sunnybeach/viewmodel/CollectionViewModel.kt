package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.paging.source.CollectionDetailPagingSource
import cn.cqautotest.sunnybeach.paging.source.CollectionPagingSource

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏 ViewModel
 */
class CollectionViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val collectionListFlow = Pager(config = PagingConfig(30),
        pagingSourceFactory = {
            CollectionPagingSource()
        }).flow.cachedIn(viewModelScope)

    val collectionDetailListFlow = Pager(config = PagingConfig(30),
        pagingSourceFactory = {
            val collectionId = savedStateHandle.get<String>(IntentKey.ID).orEmpty()
            CollectionDetailPagingSource(collectionId)
        }).flow.cachedIn(viewModelScope)
}