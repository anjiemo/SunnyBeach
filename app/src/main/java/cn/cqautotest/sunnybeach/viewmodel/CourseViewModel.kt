package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.model.course.Course
import cn.cqautotest.sunnybeach.paging.source.CoursePagingSource
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/22
 * desc   : 课程的 ViewModel
 */
class CourseViewModel : ViewModel() {

    fun getCourseChapterList(courseId: String) = Repository.getCourseChapterList(courseId)

    fun getCourseList(): Flow<PagingData<Course.CourseItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                CoursePagingSource()
            }).flow.cachedIn(viewModelScope)
    }
}