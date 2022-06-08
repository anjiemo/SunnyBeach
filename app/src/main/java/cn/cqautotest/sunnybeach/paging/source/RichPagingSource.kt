package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.network.UserNetwork
import cn.cqautotest.sunnybeach.model.RichList
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/11/01
 * desc   : 富豪榜 PagingSource
 */
class RichPagingSource : PagingSource<Int, RichList.RichUserItem>() {

    override fun getRefreshKey(state: PagingState<Int, RichList.RichUserItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RichList.RichUserItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> page is $page")
            val response = UserNetwork.getRichList()
            val responseData = response.getData()
            val prevKey = null
            val nextKey = null
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