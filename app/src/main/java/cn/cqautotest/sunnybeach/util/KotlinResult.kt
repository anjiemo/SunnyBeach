package cn.cqautotest.sunnybeach.util

import com.google.gson.Gson
import org.json.JSONObject
import timber.log.Timber

object KotlinResult {

    fun <T> toJson(gson: Gson, result: Result<T>): String {
        val value = result.getOrNull()
        val jsonValue = JSONObject(gson.toJson(value)).optString("value", "{}")
        Timber.d("toJson：===> jsonValue is $jsonValue")
        return jsonValue
    }
}
