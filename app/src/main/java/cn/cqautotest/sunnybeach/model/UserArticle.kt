package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户文章列表
 */
class UserArticle : Page<UserArticle.UserArticleItem>() {
    data class UserArticleItem(
        @SerializedName("articleType")
        val articleType: String,
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("covers")
        val covers: List<String>,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("labels")
        val labels: List<String>,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("state")
        val state: String,
        @SerializedName("thumbUp")
        val thumbUp: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("viewCount")
        val viewCount: Int,
        @SerializedName("vip")
        val vip: Boolean
    )
}