package cn.cqautotest.sunnybeach.model.course

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/21
 * desc   : 课程详情
 */
data class CourseDetail(
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("buyCount")
    val buyCount: Int,
    @SerializedName("categoryOne")
    val categoryOne: String,
    @SerializedName("categoryTwo")
    val categoryTwo: String,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("isKeepUpdate")
    val isKeepUpdate: Any,
    @SerializedName("isVipFree")
    val isVipFree: String,
    @SerializedName("labels")
    val labels: List<String>,
    @SerializedName("lev")
    val lev: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("status")
    val status: String,
    @SerializedName("teacherId")
    val teacherId: String,
    @SerializedName("teacherName")
    val teacherName: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("viewCount")
    val viewCount: Int
)