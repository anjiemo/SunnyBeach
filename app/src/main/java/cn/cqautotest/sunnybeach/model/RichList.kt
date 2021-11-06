package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/23
 * desc   : 富豪榜
 */
class RichList : ArrayList<RichList.RichUserItem>() {
    data class RichUserItem(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("sob")
        val sob: Int,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("vip")
        val vip: Boolean
    )
}