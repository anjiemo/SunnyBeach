package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.api.sob.CollectionApi
import cn.cqautotest.sunnybeach.ktx.getOrNull
import cn.cqautotest.sunnybeach.model.Bookmark
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏夹列表 PagingSource
 */
class CollectionPagingSource : PagingSource<Int, Bookmark.Content>() {

    override fun getRefreshKey(state: PagingState<Int, Bookmark.Content>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Bookmark.Content> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> page is $page")
            val response = CollectionApi.getCollectionList(page)
            val responseData = response.getOrNull() ?: return LoadResult.Error(ServiceException())
            val currentPage = responseData.number + 1
            val prevKey = if (responseData.first) null else currentPage - 1
            val nextKey = if (responseData.last) null else currentPage + 1
            LoadResult.Page(data = responseData.content, prevKey = prevKey, nextKey = nextKey)
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}
