package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.FishPondApi
import cn.cqautotest.sunnybeach.model.FishPondComment
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/13
 * desc   : 摸鱼详情评论列表 PagingSource
 */
class FishDetailCommendListPagingSource(private val momentId: String) :
    PagingSource<Int, FishPondComment.FishPondCommentItem>() {

    private val fishPondApi = ServiceCreator.create<FishPondApi>()

    override fun getRefreshKey(state: PagingState<Int, FishPondComment.FishPondCommentItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FishPondComment.FishPondCommentItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> momentId is $momentId page is $page")
            val response = fishPondApi.getFishCommendListById(momentId = momentId, page = page)
            val responseData = response.getData()
            val currentPage = responseData.currentPage
            val prevKey = if (responseData.hasPre) currentPage - 1 else null
            val nextKey = if (responseData.hasNext) currentPage + 1 else null
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