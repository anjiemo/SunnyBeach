package cn.cqautotest.sunnybeach.util

import com.google.gson.Gson
import com.huawei.hms.scankit.p.T
import org.json.JSONObject
import timber.log.Timber

object KotlinResult {

    fun <T> toJson(gson: Gson, result: Result<T>): String {
        val value = result.getOrNull()
        Timber.d("toJson：===> value is $value")
        val standardJson = gson.toJson(value)
        val jsonObject = JSONObject(standardJson)
        val jsonValue = jsonObject.optString("value", "{}")
        val exceptionInfo = JSONObject(jsonValue).tryGetExceptionInfo()
        val resultJson = if (exceptionInfo.isNotBlank()) {
            gson.toJson(exceptionInfo)
        } else {
            jsonValue
        }
        Timber.d("toJson：===> resultJson is $resultJson")
        return resultJson
    }

    private fun JSONObject.tryGetExceptionInfo(): String {
        val jsonObject = optJSONObject("exception") ?: return ""
        return jsonObject.optString("detailMessage")
    }
}
