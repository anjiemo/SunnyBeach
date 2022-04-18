package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.photo.PhotoApi
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/06
 * desc   : 壁纸 PagingSource
 */
class WallpaperPagingSource : PagingSource<Int, WallpaperBean.Res.Vertical>() {

    private val photoApi = ServiceCreator.create<PhotoApi>()

    override fun getRefreshKey(state: PagingState<Int, WallpaperBean.Res.Vertical>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WallpaperBean.Res.Vertical> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> page is $page")
            val limit = 60
            val skip = page * limit
            val response = photoApi.loadWallpaperList(limit = limit, skip = skip)
            val responseData = response.res
            val prevKey = null
            val nextKey = page + 1
            if (response.code == 0) LoadResult.Page(
                data = responseData.vertical,
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
        private const val FIRST_PAGE_INDEX = 0
    }
}