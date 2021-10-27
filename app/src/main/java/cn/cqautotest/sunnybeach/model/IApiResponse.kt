package cn.cqautotest.sunnybeach.model

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/27
 * desc   : 网络请求的数据基类
 */
interface IApiResponse<T> {

    fun getCode(): Int

    fun isSuccess(): Boolean

    fun getMessage(): String

    fun getData(): T
}