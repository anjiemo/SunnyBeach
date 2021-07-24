package cn.cqautotest.sunnybeach.util

import com.blankj.utilcode.util.GsonUtils

val Any.TAG
    get() = javaClass.simpleName

fun Any.toJson() = GsonUtils.toJson(this)

inline fun <reified T> Any.fromJson(json: String?) = GsonUtils.fromJson(json, T::class.java)