package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.model.AppUpdateInfo

/**
 * App检查更新的状态
 */
data class AppUpdateState(
    @JvmField
    val checkUpdateError: Int? = null,
    @JvmField
    val networkError: Int? = null,
    @JvmField
    val appUpdateInfo: AppUpdateInfo? = null,
    @JvmField
    val isDataValid: Boolean = false
)