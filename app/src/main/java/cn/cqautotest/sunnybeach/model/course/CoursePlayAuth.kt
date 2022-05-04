package cn.cqautotest.sunnybeach.model.course

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/22
 * desc   : 课程播放凭证
 */
data class CoursePlayAuth(
    @SerializedName("playAuth")
    val playAuth: String,
    @SerializedName("videoId")
    val videoId: String
)