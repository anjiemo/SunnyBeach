package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.http.api.sob.CourseApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/21
 *    desc   : 课程获取
 */
object CourseNetwork {

    suspend fun getCourse(page: Int) = CourseApi.getCourseList(page)

    suspend fun getCourseDetail(courseId: String) = CourseApi.getCourseDetail(courseId)

    suspend fun getCourseChapter(courseId: String) = CourseApi.getCourseChapter(courseId)

    suspend fun getCoursePlayAuth(videoId: String) = CourseApi.getCoursePlayAuth(videoId)
}