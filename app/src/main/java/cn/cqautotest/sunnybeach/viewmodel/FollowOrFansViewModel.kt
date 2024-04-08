package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.ktx.fromJson
import cn.cqautotest.sunnybeach.other.FollowType
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.paging.source.UserFollowOrFansPagingSource

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/12
 * desc   : 关注/粉丝 ViewModel
 */
class FollowOrFansViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val userFollowOrFansListStateFlow = Pager(config = PagingConfig(30),
        pagingSourceFactory = {
            val userId = savedStateHandle.get<String>(IntentKey.ID).orEmpty()
            val followStateJson = savedStateHandle.get<String>(IntentKey.OTHER)
            val followType = fromJson<FollowType>(followStateJson)
            UserFollowOrFansPagingSource(userId, followType)
        }).flow.cachedIn(viewModelScope)
}