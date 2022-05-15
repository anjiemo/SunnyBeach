package cn.cqautotest.sunnybeach.ktx

import com.hjq.gson.factory.GsonFactory

val Any.TAG
    get() = javaClass.simpleName

fun Any.toJson() = GsonFactory.getSingletonGson().toJson(this)

inline fun <reified T> fromJson(json: String?) =
    GsonFactory.getSingletonGson().fromJson(json, T::class.java)

inline fun runCatchOrPrint(block: () -> Unit) {
    runCatching(block).onFailure { it.printStackTrace() }.getOrNull()
}