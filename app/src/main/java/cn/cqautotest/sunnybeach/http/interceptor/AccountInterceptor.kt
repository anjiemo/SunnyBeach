package cn.cqautotest.sunnybeach.http.interceptor

import cn.cqautotest.sunnybeach.db.SobCacheManager
import okhttp3.Interceptor

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/26
 * desc   : 账号拦截器，根据请求的 URL 地址自动保存或添加请求头
 */
val accountInterceptor = { chain: Interceptor.Chain ->
    val request = chain.request()
    val newRequestBuilder = request.newBuilder().apply { SobCacheManager.addHeadersByNeed(request, this) }
    chain.proceed(newRequestBuilder.build()).apply { SobCacheManager.saveHeadersByNeed(request, headers) }
}