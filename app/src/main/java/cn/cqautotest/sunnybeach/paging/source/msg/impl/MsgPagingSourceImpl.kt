package cn.cqautotest.sunnybeach.paging.source.msg.impl

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.paging.source.msg.factory.AbstractMsgListFactory
import cn.cqautotest.sunnybeach.util.TAG
import cn.cqautotest.sunnybeach.util.logByDebug

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/27
 * desc   : 列表消息 PagingSource 实现类
 */
class MsgPagingSourceImpl<T : IMsgContent>(private val msgListFactory: AbstractMsgListFactory) :
    PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            logByDebug(msg = "$TAG load：===> page is $page")
            val response = try {
                msgListFactory.createMsgListByType(page)
            } catch (e: ClassCastException) {
                throw ClassCastException("Are you ok? This class can't cast to IApiResponse<IMsgPageData>.")
            }
            val responseData = response.getData()
            val prevKey = if (responseData.isFirst()) null else page - 1
            val nextKey = if (responseData.isLast()) null else page + 1
            if (response.isSuccess()) LoadResult.Page(
                data = responseData.getMsgContentList() as List<T>,
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
