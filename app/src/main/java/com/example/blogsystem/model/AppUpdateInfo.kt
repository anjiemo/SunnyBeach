package com.example.blogsystem.model

import java.io.File

data class AppUpdateInfo(
    val updateLog: String = "",
    val versionName: String = "阳光沙滩1.0",
    val versionCode: Int = 1,
    val url: String?,
    val apkSize: Long,
    val apkHash: String? = null,
    var file: File? = null,
    val forceUpdate: Boolean = false
)