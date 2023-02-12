package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName


/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/21
 * desc   : 积分规则
 */
data class IntegralRule(
    @SerializedName("createTime")
    val createTime: String,
    @SerializedName("enable")
    val enable: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("item")
    val item: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("mark")
    val mark: Any,
    @SerializedName("sob")
    val sob: Int,
    @SerializedName("tax")
    val tax: Int,
    @SerializedName("updateTime")
    val updateTime: String
)