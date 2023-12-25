package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.network.UserNetwork
import cn.cqautotest.sunnybeach.model.RichList
import cn.cqautotest.sunnybeach.util.CacheHelper
import cn.cqautotest.sunnybeach.util.CacheKey
import timber.log.Timber
import java.util.concurrent.TimeUnit

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
            takeUnless { CacheHelper.checkExpired(cacheKey, TimeUnit.HOURS.toMillis(1)) }?.let {
                val dataFromCache = CacheHelper.getFromCache<List<RichList.RichUserItem>>(cacheKey).orEmpty()
                if (dataFromCache.isNotEmpty()) {
                    return LoadResult.Page(dataFromCache, null, null)
                }
            }
            val response = UserNetwork.getRichList()
            val responseData = response.getData()
            if (response.isSuccess()) {
                LoadResult.Page<Int, RichList.RichUserItem>(responseData, null, null).also {
                    CacheHelper.saveToCache(cacheKey, responseData)
                }
            } else LoadResult.Error(ServiceException())
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }

    companion object {

        private const val FIRST_PAGE_INDEX = 1
        private const val cacheKey = CacheKey.CACHE_RICH_LIST
    }
}