package com.example.blogsystem.viewmodel.app

import com.example.blogsystem.model.AppUpdateInfo

/**
 * App检查更新的状态
 */
data class AppUpdateState(
    val checkUpdateError: Int? = null,
    val networkError: Int? = null,
    val appUpdateInfo: AppUpdateInfo? = null,
    val isDataValid: Boolean = false
)