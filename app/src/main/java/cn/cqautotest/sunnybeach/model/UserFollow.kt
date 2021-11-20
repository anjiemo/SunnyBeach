package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 当前用户关注的用户列表
 */
data class UserFollow(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean,
    @SerializedName("hasPre")
    val hasPre: Boolean,
    @SerializedName("list")
    val list: List<UserFollowItem>,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPage")
    val totalPage: Int
) {
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