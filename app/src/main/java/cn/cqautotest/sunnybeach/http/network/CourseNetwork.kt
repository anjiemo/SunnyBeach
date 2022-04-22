package cn.cqautotest.sunnybeach.http.network

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/21
 *    desc   : 课程获取
 */
object CourseNetwork : INetworkApi {

    suspend fun getCourse(page: Int) = courseApi.getCourseList(page)

    suspend fun getCourseDetail(courseId: String) = courseApi.getCourseDetail(courseId)

    suspend fun getCourseChapter(courseId: String) = courseApi.getCourseChapter(courseId)

    suspend fun getCoursePlayAuth(videoId: String) = courseApi.getCoursePlayAuth(videoId)
}