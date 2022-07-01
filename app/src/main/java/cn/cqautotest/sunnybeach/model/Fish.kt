package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/07
 * desc   : 摸鱼的数据bean类
 */
class Fish : Page<Fish.FishItem>() {
    data class FishItem(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("commentCount")
        val commentCount: Int,
        @SerializedName("company")
        val company: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("images")
        val images: List<String>,
        @SerializedName("linkCover")
        val linkCover: String,
        @SerializedName("linkTitle")
        val linkTitle: String,
        @SerializedName("linkUrl")
        val linkUrl: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("position")
        val position: String?,
        @SerializedName("thumbUpCount")
        val thumbUpCount: Int,
        @SerializedName("thumbUpList")
        val thumbUpList: MutableList<String>,
        @SerializedName("topicId")
        val topicId: String,
        @SerializedName("topicName")
        val topicName: String,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("vip")
        val vip: Boolean
    )
}