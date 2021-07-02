@file:JvmName("LiveDataUtils")
package cn.cqautotest.sunnybeach.utils

import androidx.lifecycle.LiveData

val <T: Any> LiveData<T>.requiredValue: T get() =
    value ?: error("Non-null value cannot be accessed before value is first set.")