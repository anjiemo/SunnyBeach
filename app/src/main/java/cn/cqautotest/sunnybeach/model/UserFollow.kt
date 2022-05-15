package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 当前用户关注的用户列表
 */
class UserFollow : Page<UserFollow.UserFollowItem>() {
    data class UserFollowItem(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("relative")
        val relative: Int,
        @SerializedName("sign")
        val sign: String?,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("vip")
        val vip: Boolean
    )
}