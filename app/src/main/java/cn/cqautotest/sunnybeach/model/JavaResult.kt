package cn.cqautotest.sunnybeach.model

object JavaResult {

    fun success(message: String) = Result(true, message)

    fun failure(message: String) = Result(false, message)

    fun failure(case: Throwable) = Result(false, case.message.toString())

    data class Result(val isSuccess: Boolean, val message: String)
}