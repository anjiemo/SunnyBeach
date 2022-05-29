package cn.cqautotest.sunnybeach.ktx

import com.google.gson.reflect.TypeToken
import com.hjq.gson.factory.GsonFactory

val Any.TAG: String
    get() = javaClass.simpleName

fun Any.toJson(): String = GsonFactory.getSingletonGson().toJson(this)

inline fun <reified T> fromJson(json: String?): T =
    GsonFactory.getSingletonGson().fromJson(json, T::class.java)

inline fun <reified T> fromJsonByTypeToken(json: String?): T =
    GsonFactory.getSingletonGson().fromJson(json, object : TypeToken<T>() {}.type)