package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.UserQa
import cn.cqautotest.sunnybeach.paging.source.UserQaPagingSource
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 问答 ViewModel
 */
class QaViewModel : ViewModel() {

    fun getUserQaList(userId: String): Flow<PagingData<UserQa.Content>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                UserQaPagingSource(userId)
            }).flow.cachedIn(viewModelScope)
    }
}