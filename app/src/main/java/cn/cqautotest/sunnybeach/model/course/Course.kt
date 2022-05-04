package cn.cqautotest.sunnybeach.model.course

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/21
 * desc   : 课程
 */
data class Course(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean,
    @SerializedName("hasPre")
    val hasPre: Boolean,
    @SerializedName("list")
    val list: List<CourseItem>,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPage")
    val totalPage: Int
) {
    data class CourseItem(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("buyCount")
        val buyCount: Int,
        @SerializedName("cover")
        val cover: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("keepUpdate")
        val keepUpdate: String,
        @SerializedName("lev")
        val lev: String,
        @SerializedName("price")
        val price: Double,
        @SerializedName("teacherId")
        val teacherId: String,
        @SerializedName("teacherName")
        val teacherName: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("viewCount")
        val viewCount: Int
    )
}