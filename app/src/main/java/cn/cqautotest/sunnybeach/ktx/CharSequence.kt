package cn.cqautotest.sunnybeach.ktx

fun CharSequence?.orEmpty(): String = this?.toString() ?: ""