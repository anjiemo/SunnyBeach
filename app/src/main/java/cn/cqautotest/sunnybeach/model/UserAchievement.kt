package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 用户成就
 */
data class UserAchievement(
    @SerializedName("articleDxView")
    val articleDxView: Int,
    @SerializedName("articleTotal")
    val articleTotal: Int,
    @SerializedName("asCount")
    val asCount: Int,
    @SerializedName("atotalView")
    val atotalView: Int,
    @SerializedName("fansCount")
    val fansCount: Int,
    @SerializedName("fansDx")
    val fansDx: Int,
    @SerializedName("favorites")
    val favorites: Int,
    @SerializedName("followCount")
    val followCount: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("momentCount")
    val momentCount: Int,
    @SerializedName("resolveCount")
    val resolveCount: Int,
    @SerializedName("shareDxView")
    val shareDxView: Int,
    @SerializedName("shareTotal")
    val shareTotal: Int,
    @SerializedName("sob")
    val sob: Int,
    @SerializedName("sobDx")
    val sobDx: Int,
    @SerializedName("stotalView")
    val stotalView: Int,
    @SerializedName("thumbUpDx")
    val thumbUpDx: Int,
    @SerializedName("thumbUpTotal")
    val thumbUpTotal: Int,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("wendaTotal")
    val wendaTotal: Int
)