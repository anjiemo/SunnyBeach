package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.Bookmark
import cn.cqautotest.sunnybeach.model.BookmarkDetail
import cn.cqautotest.sunnybeach.paging.source.CollectionDetailPagingSource
import cn.cqautotest.sunnybeach.paging.source.CollectionPagingSource
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏 ViewModel
 */
class CollectionViewModel : ViewModel() {

    fun loadCollectionList(): Flow<PagingData<Bookmark.Content>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                CollectionPagingSource()
            }).flow.cachedIn(viewModelScope)
    }

    fun loadCollectionDetailListById(collectionId: String): Flow<PagingData<BookmarkDetail.Content>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                CollectionDetailPagingSource(collectionId)
            }).flow.cachedIn(viewModelScope)
    }
}