package cn.cqautotest.sunnybeach.utils

import com.blankj.utilcode.util.GsonUtils

val Any.TAG
    get() = javaClass.simpleName

fun Any.toJson() = GsonUtils.toJson(this)
