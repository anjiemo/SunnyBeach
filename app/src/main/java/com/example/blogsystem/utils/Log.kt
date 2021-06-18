package com.example.blogsystem.utils

import android.util.Log

/**
 * 是否启用 log
 */
private fun isEnableLog() = AppConfig.isDebug()

fun Any.logByError(tag: String = TAG, msg: String) {
    if (isEnableLog().not()) return
    Log.e(tag, msg)
}

fun Any.logByWarning(tag: String = TAG, msg: String) {
    if (isEnableLog().not()) return
    Log.w(tag, msg)
}

fun Any.logByInfo(tag: String = TAG, msg: String) {
    if (isEnableLog().not()) return
    Log.i(tag, msg)
}

fun Any.logByDebug(tag: String = TAG, msg: String) {
    if (isEnableLog().not()) return
    Log.d(tag, msg)
}