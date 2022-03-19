package cn.cqautotest.sunnybeach.model

/**
 * 短信验证码信息
 */
data class SmsInfo(private val phoneNumber: String, private val verifyCode: String)