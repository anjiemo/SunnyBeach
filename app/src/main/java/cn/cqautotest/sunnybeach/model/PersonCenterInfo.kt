package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/08
 * desc   : 个人中心用户信息的数据bean类
 */
data class PersonCenterInfo(
    @SerializedName("area")
    val area: String = "",
    @SerializedName("avatar")
    val avatar: String = "",
    @SerializedName("company")
    val company: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("goodAt")
    val goodAt: String = "",
    @SerializedName("isvIP")
    val isvIP: String = "",
    @SerializedName("nickname")
    val nickname: String = "",
    @SerializedName("phoneNum")
    val phoneNum: String = "",
    @SerializedName("position")
    val position: String = "",
    @SerializedName("sex")
    val sex: Int = 0,
    @SerializedName("sign")
    val sign: String = "",
    @SerializedName("userId")
    val userId: String = ""
)