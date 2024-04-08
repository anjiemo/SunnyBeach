package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.paging.source.CourseChapterPagingSource
import cn.cqautotest.sunnybeach.paging.source.CoursePagingSource

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/22
 * desc   : 课程的 ViewModel
 */
class CourseViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val courseChapterListFlow = Pager(config = PagingConfig(30),
        pagingSourceFactory = {
            val courseId = savedStateHandle.get<String>(IntentKey.ID).orEmpty()
            CourseChapterPagingSource(courseId)
        }).flow.cachedIn(viewModelScope)

    val courseListFlow = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = {
            CoursePagingSource()
        }).flow.cachedIn(viewModelScope)

    fun getCoursePlayAuth(videoId: String) = Repository.getCoursePlayAuth(videoId)
}