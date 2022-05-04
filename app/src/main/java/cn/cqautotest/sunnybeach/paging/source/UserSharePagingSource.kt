package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.network.ShareNetwork
import cn.cqautotest.sunnybeach.model.UserShare
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/12
 * desc   : 指定用户的分享 PagingSource
 */
class UserSharePagingSource(private val userId: String) :
    PagingSource<Int, UserShare.Content>() {

    override fun getRefreshKey(state: PagingState<Int, UserShare.Content>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserShare.Content> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> userId is $userId page is $page")
            val response = ShareNetwork.loadUserShareList(userId = userId, page = page)
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