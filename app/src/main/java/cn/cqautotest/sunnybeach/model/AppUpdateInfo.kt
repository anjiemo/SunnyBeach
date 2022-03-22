package cn.cqautotest.sunnybeach.model

import java.io.File

data class AppUpdateInfo(
    @JvmField
    val updateLog: String = "",
    @JvmField
    val versionName: String = "阳光沙滩1.0",
    @JvmField
    val versionCode: Int = 1,
    @JvmField
    val minVersionCode: Int = 1,
    @JvmField
    val url: String?,
    @JvmField
    val apkSize: Long,
    @JvmField
    val apkHash: String? = null,
    @JvmField
    var file: File? = null,
    @JvmField
    val forceUpdate: Boolean = false
)