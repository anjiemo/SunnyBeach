package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.api.sob.CourseApi
import cn.cqautotest.sunnybeach.model.course.Course
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/22
 * desc   : 课程 PagingSource
 */
class CoursePagingSource : PagingSource<Int, Course.CourseItem>() {

    override fun getRefreshKey(state: PagingState<Int, Course.CourseItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Course.CourseItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> page is $page")
            val response = CourseApi.getCourseList(page = page)
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