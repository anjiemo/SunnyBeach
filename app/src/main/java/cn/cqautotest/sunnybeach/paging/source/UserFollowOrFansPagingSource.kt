package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.network.FansNetwork
import cn.cqautotest.sunnybeach.http.network.FollowNetwork
import cn.cqautotest.sunnybeach.model.UserFollow
import cn.cqautotest.sunnybeach.other.FollowType
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 指定用户的关注/粉丝 PagingSource
 */
class UserFollowOrFansPagingSource(private val userId: String, private val followType: FollowType) :
    PagingSource<Int, UserFollow.UserFollowItem>() {

    override fun getRefreshKey(state: PagingState<Int, UserFollow.UserFollowItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserFollow.UserFollowItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> userId is $userId page is $page")
            val response = when (followType) {
                FollowType.FOLLOW -> FollowNetwork.loadUserFollowList(userId = userId, page = page)
                FollowType.FANS -> FansNetwork.loadUserFansList(userId = userId, page = page)
            }
            val responseData = response.getData()
            val prevKey = if (responseData.hasPre) page - 1 else null
            val nextKey = if (responseData.hasNext) page + 1 else null
            if (response.isSuccess()) LoadResult.Page(
                data = responseData.list,
                prevKey = prevKey,
                nextKey = nextKey
            )
            else LoadResult.Error(ServiceException())
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}