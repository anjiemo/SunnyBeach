package cn.cqautotest.sunnybeach.model

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/08
 * desc   : 个人中心用户信息的数据bean类
 */
data class PersonCenterInfo(
    val area: String,
    val avatar: String,
    val company: String?,
    val email: String,
    val goodAt: String,
    val isvIP: String,
    val nickname: String,
    val phoneNum: String,
    val position: String?,
    val sex: Int,
    val sign: String,
    val userId: String
)