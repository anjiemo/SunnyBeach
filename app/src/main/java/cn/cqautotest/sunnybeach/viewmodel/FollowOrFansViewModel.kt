package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.UserFollow
import cn.cqautotest.sunnybeach.other.FollowType
import cn.cqautotest.sunnybeach.paging.source.UserFollowOrFansPagingSource
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/12
 * desc   : 关注/粉丝 ViewModel
 */
class FollowOrFansViewModel : ViewModel() {

    fun loadUserFollowOrFansListByState(
        userId: String,
        followType: FollowType
    ): Flow<PagingData<UserFollow.UserFollowItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                UserFollowOrFansPagingSource(userId, followType)
            }).flow.cachedIn(viewModelScope)
    }
}