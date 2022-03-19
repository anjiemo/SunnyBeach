package cn.cqautotest.sunnybeach.model

/**
 * 修改密码
 */
data class ModifyPwd(
    private val oldPwd: String,
    private val newPwd: String,
    private val captcha: String
)