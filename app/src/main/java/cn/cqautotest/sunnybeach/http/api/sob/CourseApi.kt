package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.annotation.baseurl.SobBaseUrl
import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.course.Course
import cn.cqautotest.sunnybeach.model.course.CourseChapter
import cn.cqautotest.sunnybeach.model.course.CourseDetail
import cn.cqautotest.sunnybeach.model.course.CoursePlayAuth
import retrofit2.http.GET
import retrofit2.http.Path

@SobBaseUrl
interface CourseApi {

    /**
     * 获取课程列表
     */
    @GET("ct/edu/course/list/{page}")
    suspend fun getCourseList(@Path("page") page: Int): ApiResponse<Course>

    /**
     * 获取课程详情
     */
    @GET("ct/edu/course/{courseId}")
    suspend fun getCourseDetail(@Path("courseId") courseId: String): ApiResponse<CourseDetail>

    /**
     * 获取课程章节内容
     */
    @GET("ct/edu/course/chapter/{courseId}")
    suspend fun getCourseChapter(@Path("courseId") courseId: String): ApiResponse<CourseChapter>

    /**
     * 检查课程是否有购买
     */
    @GET("ct/course-buy/{courseId}")
    suspend fun checkCourseHasBuy(@Path("courseId") courseId: String): ApiResponse<Any>

    /**
     * 获取课程播放凭证
     */
    @GET("ct/video/certification/{videoId}")
    suspend fun getCoursePlayAuth(@Path("videoId") videoId: String): ApiResponse<CoursePlayAuth>

    companion object : CourseApi by ServiceCreator.create()
}