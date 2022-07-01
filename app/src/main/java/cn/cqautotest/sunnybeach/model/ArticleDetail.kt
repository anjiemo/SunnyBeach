package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/27
 * desc   : 文章详情
 */
data class ArticleDetail(
    @SerializedName("articleType")
    val articleType: String,
    @JvmField
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("contentType")
    val contentType: String,
    @SerializedName("covers")
    val covers: List<String>,
    @SerializedName("createTime")
    val createTime: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("isComment")
    val isComment: String,
    @SerializedName("isTop")
    val isTop: String,
    @SerializedName("isVip")
    val isVip: String,
    @SerializedName("labels")
    val labels: List<String>,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("recommend")
    val recommend: Int,
    @SerializedName("state")
    val state: String,
    @SerializedName("thumbUp")
    val thumbUp: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("viewCount")
    val viewCount: Int
)