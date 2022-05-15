package cn.cqautotest.sunnybeach.ktx

import cn.cqautotest.sunnybeach.model.IApiResponse

inline fun <reified T : Any> IApiResponse<T>.getOrNull(): T? = if (isSuccess()) getData() else null