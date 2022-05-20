package cn.cqautotest.sunnybeach.ktx

import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.model.IApiResponse

inline fun <reified T : Any> IApiResponse<T>.getOrNull(): T? = if (isSuccess()) getData() else null

inline fun <reified T : Any> IApiResponse<T>.getOrElse(defaultValue: () -> T): T = if (isSuccess()) getData() else defaultValue.invoke()

fun <T, R> IApiResponse<T>.toErrorResult(block: IApiResponse<T>.() -> String = { getMessage() }): Result<R> =
    Result.failure(ServiceException(block.invoke(this)))