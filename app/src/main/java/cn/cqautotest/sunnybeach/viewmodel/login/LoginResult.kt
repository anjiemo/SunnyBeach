package cn.cqautotest.sunnybeach.viewmodel.login

/**
 * 身份验证结果：成功（用户详细信息）或错误消息
 */
data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: String? = null
)