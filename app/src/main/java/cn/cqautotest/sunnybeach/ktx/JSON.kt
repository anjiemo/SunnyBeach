package cn.cqautotest.sunnybeach.ktx

import org.json.JSONObject

// region Kotlin-DSL

// 更多用法查阅：https://github.com/Kotlin/KEEP/blob/master/proposals/context-receivers.md

fun json(build: JSONObject.() -> Unit) = JSONObject().apply { build() }

context(jsonObject: JSONObject)
infix fun String.by(build: JSONObject.() -> Unit): JSONObject = jsonObject.put(this, JSONObject().build())

context(jsonObject: JSONObject)
infix fun String.by(value: Any): JSONObject = jsonObject.put(this, value)

// endregion
