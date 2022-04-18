package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.sob.HomeApi
import cn.cqautotest.sunnybeach.model.QaInfo
import cn.cqautotest.sunnybeach.other.QaState
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/18
 * desc   : 问答 PagingSource
 */
class QaPagingSource(private val qaState: QaState) : PagingSource<Int, QaInfo.QaInfoItem>() {

    private val homeApi = ServiceCreator.create<HomeApi>()

    override fun getRefreshKey(state: PagingState<Int, QaInfo.QaInfoItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QaInfo.QaInfoItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> qaState is $qaState page is $page")
            val response = homeApi.getQaList(page = page, state = qaState.value)
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