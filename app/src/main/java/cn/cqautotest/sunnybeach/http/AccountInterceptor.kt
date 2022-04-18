package cn.cqautotest.sunnybeach.http

import cn.cqautotest.sunnybeach.db.SobCacheManager
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/26
 * desc   : 账号拦截器，根据请求的 URL 地址自动保存或添加请求头
 */
class AccountInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val requestBuilder = request.newBuilder()

        // 按需要添加请求头
        SobCacheManager.addHeadersByNeed(request, requestBuilder)

        val response = try {
            chain.proceed(requestBuilder.build())
        } catch (t: Throwable) {
            throw when (t) {
                is SocketTimeoutException -> {
                    val errorMsg = buildString {
                        append(t.message)
                        append("\n")
                        append("the request url is ===> $url")
                    }
                    SocketTimeoutException(errorMsg)
                }
                else -> t
            }
        }
        val headers = response.headers

        // 根据需要保存请求头
        SobCacheManager.saveHeadersByNeed(request, headers)

        return response
    }
}