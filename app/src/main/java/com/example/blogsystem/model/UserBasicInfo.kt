package com.example.blogsystem.model

import com.example.blogsystem.utils.DEFAULT_AVATAR_URL
import com.google.gson.annotations.SerializedName

/**
 * 用户基本信息
 */
data class UserBasicInfo(
    @SerializedName("avatar")
    val avatar: String? = DEFAULT_AVATAR_URL,
    @SerializedName("fansCount")
    val fansCount: Any,
    @SerializedName("followCount")
    val followCount: Any,
    @SerializedName("id")
    val id: String,
    @SerializedName("isVip")
    val isVip: String,
    @SerializedName("lev")
    val lev: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("roles")
    val roles: String,
    @SerializedName("token")
    val token: String
)