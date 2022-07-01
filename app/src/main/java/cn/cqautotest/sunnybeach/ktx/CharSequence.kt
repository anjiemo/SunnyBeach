package cn.cqautotest.sunnybeach.ktx

public inline fun CharSequence?.orEmpty(): String = this?.toString() ?: ""