@file:JvmName("LogUtils")
package cn.cqautotest.sunnybeach.util

import cn.cqautotest.sunnybeach.other.AppConfig
import timber.log.Timber

/**
 * 是否启用 log
 */
private fun isEnableLog() = AppConfig.isDebug()

@JvmOverloads
fun Any.logByError(tag: String = TAG, msg: String) {
    if (isEnableLog().not()) return
    Timber.e(tag, msg)
}

@JvmOverloads
fun Any.logByWarning(tag: String = TAG, msg: String) {
    if (isEnableLog().not()) return
    Timber.tag(tag).w(msg)
}

@JvmOverloads
fun Any.logByInfo(tag: String = TAG, msg: String) {
    if (isEnableLog().not()) return
    Timber.tag(tag).i(msg)
}

@JvmOverloads
fun Any.logByDebug(tag: String = TAG, msg: String) {
    if (isEnableLog().not()) return
    Timber.tag(tag).d(msg)
}