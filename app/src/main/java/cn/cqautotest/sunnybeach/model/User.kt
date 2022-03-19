package cn.cqautotest.sunnybeach.model

/**
 * 用户账号信息
 */
data class User(
    private val phoneNum: String,
    private val password: String,
    private val nickname: String = ""
)
