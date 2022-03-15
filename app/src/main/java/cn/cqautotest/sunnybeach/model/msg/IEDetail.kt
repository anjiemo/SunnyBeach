package cn.cqautotest.sunnybeach.model.msg

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/14
 * desc   : 收支（Income & Expenditures）明细
 */
data class IEDetail(
    @SerializedName("content")
    val content: String,
    @SerializedName("createTime")
    val createTime: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("sob")
    val sob: Int,
    @SerializedName("tax")
    val tax: Int,
    @SerializedName("userId")
    val userId: String
)