package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.SearchResult
import cn.cqautotest.sunnybeach.other.SearchType
import cn.cqautotest.sunnybeach.other.SortType
import cn.cqautotest.sunnybeach.paging.source.SearchPagingSource
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索 ViewModel
 */
class SearchViewModel : ViewModel() {

    private val _keywordsLiveData = MutableLiveData<String>()
    val keywordsLiveData: LiveData<String> get() = _keywordsLiveData

    fun setKeywords(keywords: String) {
        _keywordsLiveData.value = keywords
    }

    fun searchByKeywords(keyword: String, searchType: SearchType, sortType: SortType): Flow<PagingData<SearchResult.SearchResultItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                SearchPagingSource(keyword = keyword, searchType = searchType, sortType = sortType)
            }).flow.cachedIn(viewModelScope)
    }
}