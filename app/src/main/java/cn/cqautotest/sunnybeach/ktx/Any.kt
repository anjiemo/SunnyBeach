package cn.cqautotest.sunnybeach.ktx

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.gson.factory.GsonFactory

val Any.TAG: String
    get() = javaClass.simpleName

fun Any?.toJson(gson: Gson): String? = gson.toJson(this)

inline fun <reified T> fromJson(gson: Gson, json: String?): T = gson.fromJson(json, object : TypeToken<T>() {}.type)

inline fun <reified T> fromJsonOrNull(gson: Gson, json: String?): T? =
    gson.fromJson(json, object : TypeToken<T>() {}.type)

inline fun <reified T> fromJsonOrNull(json: String?): T? =
    GsonFactory.getSingletonGson().fromJson(json, object : TypeToken<T>() {}.type)

fun Any?.toJson(): String? = GsonFactory.getSingletonGson().toJson(this)

inline fun <reified T> fromJson(json: String?): T =
    GsonFactory.getSingletonGson().fromJson(json, object : TypeToken<T>() {}.type)
