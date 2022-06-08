package cn.cqautotest.sunnybeach.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.ServiceCreator
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

    private val courseApi = ServiceCreator.create<CourseApi>()

    override fun getRefreshKey(state: PagingState<Int, CourseChapterListAdapter.Type>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CourseChapterListAdapter.Type> {
        return try {
            Timber.d("load：===> courseId is $courseId")
            val response = courseApi.getCourseChapter(courseId = courseId)
            val responseData = response.getData()
            val prevKey = null
            val nextKey = null
            val data = arrayListOf<CourseChapterListAdapter.Type>()
            responseData.onEach {
                data.add(it)
                it.children.onEach { children ->
                    data.add(children)
                }
            }
            if (response.isSuccess()) LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
            else LoadResult.Error(ServiceException())
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }
}