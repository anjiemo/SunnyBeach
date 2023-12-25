package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.network.FishNetwork
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.Fish
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/22
 * desc   : 摸鱼详情 PagingSource
 */
class FishDetailPagingSource(private val momentId: String) :
    PagingSource<Int, Fish.FishItem>() {

    override fun getRefreshKey(state: PagingState<Int, Fish.FishItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Fish.FishItem> {
        return try {
            Timber.d("load：===> momentId is $momentId")
            val response = FishNetwork.loadFishDetailById(momentId = momentId)
            val responseData = response.getData()
            println("load：===> responseData is ${responseData.toJson()}")
            val prevKey = null
            val nextKey = null
            if (response.isSuccess()) LoadResult.Page(data = listOf(responseData), prevKey = prevKey, nextKey = nextKey)
            else LoadResult.Error(ServiceException())
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }
}