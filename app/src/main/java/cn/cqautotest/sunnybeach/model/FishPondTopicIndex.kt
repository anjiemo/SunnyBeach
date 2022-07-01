package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/10
 * desc   : 摸鱼首页话题列表的数据bean类
 */
class FishPondTopicIndex : ArrayList<FishPondTopicIndex.FishPondTopicIndexItem>() {
    data class FishPondTopicIndexItem(
        @SerializedName("contentCount")
        val contentCount: Int,
        @SerializedName("cover")
        val cover: Any,
        @SerializedName("createTime")
        val createTime: Any,
        @SerializedName("description")
        val description: Any,
        @SerializedName("followCount")
        val followCount: Int,
        @SerializedName("id")
        val id: String,
        @SerializedName("order")
        val order: Int,
        @SerializedName("topicName")
        val topicName: String,
        @SerializedName("updateTime")
        val updateTime: Any
    )
}