package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.UserShare
import cn.cqautotest.sunnybeach.paging.source.UserSharePagingSource
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/12
 * desc   : 分享 ViewModel
 */
class ShareViewModel : ViewModel() {

    fun loadUserShareList(userId: String): Flow<PagingData<UserShare.Content>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                UserSharePagingSource(userId)
            }).flow.cachedIn(viewModelScope)
    }
}