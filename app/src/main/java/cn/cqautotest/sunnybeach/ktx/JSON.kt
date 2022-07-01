package cn.cqautotest.sunnybeach.ktx

import org.json.JSONObject

// region Kotlin-DSL

// 更多用法查阅：https://github.com/Kotlin/KEEP/blob/master/proposals/context-receivers.md

fun json(build: JSONObject.() -> Unit) = JSONObject().apply { build() }

context(JSONObject)
        infix fun String.by(build: JSONObject.() -> Unit): JSONObject = put(this, JSONObject().build())

context(JSONObject)
        infix fun String.by(value: Any): JSONObject = put(this, value)

// endregion
