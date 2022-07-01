package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 用户分享列表
 */
class UserShare : Page<UserShare.Content>() {
    data class Content(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("categoryName")
        val categoryName: Any,
        @SerializedName("cover")
        val cover: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("description")
        val description: Any,
        @SerializedName("id")
        val id: String,
        @SerializedName("labels")
        val labels: List<String>,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("state")
        val state: Any,
        @SerializedName("thumbUp")
        val thumbUp: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("url")
        val url: String,
        @SerializedName("userId")
        val userId: Any,
        @SerializedName("viewCount")
        val viewCount: Int,
        @SerializedName("vip")
        val vip: Boolean
    )
}