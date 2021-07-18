package cn.cqautotest.sunnybeach.util

import android.content.Context
import android.content.Intent

fun Context.startActivity(clazz: Class<*>) {
    startActivity(Intent(this, clazz))
}

inline fun <reified T> Context.startActivity() {
    startActivity(T::class.java)
}