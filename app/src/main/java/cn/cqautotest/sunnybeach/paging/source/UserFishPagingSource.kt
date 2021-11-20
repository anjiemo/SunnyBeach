package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.viewmodel.app.FishNetwork
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 指定用户的摸鱼 PagingSource
 */
class UserFishPagingSource(private val userId: String) :
    PagingSource<Int, Fish.FishItem>() {

    override fun getRefreshKey(state: PagingState<Int, Fish.FishItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Fish.FishItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> userId is $userId page is $page")
            val response = FishNetwork.loadUserFishList(userId = userId, page = page)
            val responseData = response.getData()
            val prevKey = null
            val nextKey = if (responseData.isNotEmpty()) page + 1 else null
            if (response.isSuccess()) LoadResult.Page(
                data = responseData,
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