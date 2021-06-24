package cn.cqautotest.sunnybeach.viewmodel.app

/**
 * App检查更新结果：成功（App详细信息）或错误消息
 */
data class AppUpdateResult(
    val success: AppUpdateInUserView? = null,
    val error: String? = null
)