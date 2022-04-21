package cn.cqautotest.sunnybeach.model.course

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/21
 * desc   : 课程章节列表
 */
class CourseChapter : ArrayList<CourseChapter.CourseChapterItem>() {
    data class CourseChapterItem(
        // 章节内容（章节里的视频）
        @SerializedName("children")
        val children: List<Children>,
        // 课程 ID
        @SerializedName("courseId")
        val courseId: String,
        // 章节介绍
        @SerializedName("description")
        val description: String,
        // ID
        @SerializedName("id")
        val id: String,
        // 顺序
        @SerializedName("sort")
        val sort: Int,
        // 标题
        @SerializedName("title")
        val title: String
    ) {
        data class Children(
            // 章节 ID
            @SerializedName("chapterId")
            val chapterId: String,
            // 课程 ID
            @SerializedName("courseId")
            val courseId: String,
            // 视频时长（秒）
            @SerializedName("duration")
            val duration: Int,
            // 视频 ID
            @SerializedName("id")
            val id: String,
            // 是否可以试听：0收费 1免费
            @SerializedName("isFree")
            val isFree: Boolean,
            // 播放次数
            @SerializedName("playCount")
            val playCount: Int,
            // 排序字段
            @SerializedName("sort")
            val sort: Int,
            // 节点名称
            @SerializedName("title")
            val title: String
        )
    }
}