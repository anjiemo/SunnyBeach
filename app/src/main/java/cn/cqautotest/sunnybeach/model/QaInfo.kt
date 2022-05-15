package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 首页问答
 */
class QaInfo : Page<QaInfo.QaInfoItem>() {
    data class QaInfoItem(
        @SerializedName("answerCount")
        val answerCount: Int,
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("categoryId")
        val categoryId: String,
        @SerializedName("categoryName")
        val categoryName: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("isResolve")
        val isResolve: String,
        @SerializedName("isVip")
        val isVip: String,
        @SerializedName("label")
        val label: Any,
        @SerializedName("labels")
        val labels: List<String>,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("sob")
        val sob: Int,
        @SerializedName("thumbUp")
        val thumbUp: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("viewCount")
        val viewCount: Int
    )
}