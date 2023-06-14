package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.api.sob.CourseApi
import cn.cqautotest.sunnybeach.ui.adapter.CourseChapterListAdapter
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/22
 * desc   : 课程章节 PagingSource（没有分页）
 */
class CourseChapterPagingSource(private val courseId: String) : PagingSource<Int, CourseChapterListAdapter.Type>() {

    override fun getRefreshKey(state: PagingState<Int, CourseChapterListAdapter.Type>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CourseChapterListAdapter.Type> {
        return try {
            Timber.d("load：===> courseId is $courseId")
            val response = CourseApi.getCourseChapter(courseId = courseId)
            val responseData = response.getData()
            val data = responseData.flatMap { listOf(it) + it.children }
            if (response.isSuccess()) LoadResult.Page(data, null, null)
            else LoadResult.Error(ServiceException())
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }
}