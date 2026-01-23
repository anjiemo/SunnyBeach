package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.aliyun.AliyunVideoUrlInfo
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.paging.source.CourseChapterPagingSource
import cn.cqautotest.sunnybeach.paging.source.CoursePagingSource
import cn.cqautotest.sunnybeach.repository.Repository
import cn.cqautotest.sunnybeach.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/22
 * desc   : 课程的 ViewModel
 */
@HiltViewModel
class CourseViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _videoPlayState = MutableStateFlow<VideoPlayState>(VideoPlayState.Idle)
    val videoPlayState = _videoPlayState.asStateFlow()

    val courseChapterListFlow = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = {
            val courseId = savedStateHandle.get<String>(IntentKey.ID).orEmpty()
            CourseChapterPagingSource(courseId)
        }).flow.cachedIn(viewModelScope)

    val courseListFlow = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = {
            CoursePagingSource()
        }).flow.cachedIn(viewModelScope)

    /**
     * 获取视频播放链接
     */
    fun fetchVideoInfo(videoId: String) {
        viewModelScope.launch {
            videoRepository.getVideoPlayInfo(videoId)
                .onStart { _videoPlayState.value = VideoPlayState.Loading }
                .collect { result ->
                    _videoPlayState.value = result.fold(
                        onSuccess = { VideoPlayState.Success(it) },
                        onFailure = { VideoPlayState.Error(it) }
                    )
                }
        }
    }

    /**
     * 重置视频播放状态
     */
    fun resetVideoPlayState() {
        _videoPlayState.value = VideoPlayState.Idle
    }

    fun getCoursePlayAuth(videoId: String) = Repository.getCoursePlayAuth(videoId)

    /**
     * 视频播放状态定义
     */
    sealed class VideoPlayState {
        object Idle : VideoPlayState()
        object Loading : VideoPlayState()
        data class Success(val info: AliyunVideoUrlInfo) : VideoPlayState()
        data class Error(val throwable: Throwable) : VideoPlayState()
    }
}