package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 用户信息
 */
data class UserInfo(
    @SerializedName("area")
    val area: Any?,
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("company")
    val company: String?,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("position")
    val position: String?,
    @SerializedName("sign")
    val sign: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("vip")
    val vip: Boolean,
    @SerializedName("cover")
    val cover: String
)