package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.api.sob.FishPondApi
import cn.cqautotest.sunnybeach.ktx.getOrNull
import cn.cqautotest.sunnybeach.model.Fish
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/06
 * desc   : 摸鱼 PagingSource
 */
class FishPagingSource(private val topicId: String) :
    PagingSource<Int, Fish.FishItem>() {

    override fun getRefreshKey(state: PagingState<Int, Fish.FishItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Fish.FishItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> topicId is $topicId page is $page")
            val response = FishPondApi.loadFishListById(topicId = topicId, page = page)
            val responseData = response.getOrNull() ?: return LoadResult.Error(ServiceException())
            val currentPage = responseData.currentPage
            val prevKey = if (responseData.hasPre) currentPage - 1 else null
            val nextKey = if (responseData.hasNext) currentPage + 1 else null
            LoadResult.Page(data = responseData.list, prevKey = prevKey, nextKey = nextKey)
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}