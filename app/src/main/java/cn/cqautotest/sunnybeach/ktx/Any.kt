package cn.cqautotest.sunnybeach.ktx

import com.hjq.gson.factory.GsonFactory

val Any.TAG: String
    get() = javaClass.simpleName

fun Any.toJson(): String = GsonFactory.getSingletonGson().toJson(this)

inline fun <reified T> fromJson(json: String?): T =
    GsonFactory.getSingletonGson().fromJson(json, T::class.java)
