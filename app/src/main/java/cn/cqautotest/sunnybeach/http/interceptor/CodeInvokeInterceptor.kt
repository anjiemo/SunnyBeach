package cn.cqautotest.sunnybeach.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Invocation
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/06/26
 * desc   : 代码调用拦截器
 */
class CodeInvokeInterceptor(private val loggingInterceptor: HttpLoggingInterceptor): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        request.tag(Invocation::class.java)?.let { invocation ->
            Timber.d("CodeInvokeInterceptor：===> invocation is $invocation")
        }
        return loggingInterceptor.intercept(chain)
    }
}