package cn.cqautotest.sunnybeach.http.interceptor

import cn.cqautotest.sunnybeach.http.annotation.BaseUrl
import cn.cqautotest.sunnybeach.ktx.getCustomAnnotation
import cn.cqautotest.sunnybeach.util.BASE_URL
import okhttp3.Interceptor
import okhttp3.Request
import retrofit2.Invocation

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/13
 * desc   : BaseUrl 拦截器，根据请求接口的注解来设置请求的 BaseUrl ，
 * 不能与 @SobClient 等 @xxxClient 类的注解同时使用，@xxxClient 类的注解优先级比 @xxxBaseUrl 类的注解高。
 */
val baseUrlInterceptor = { chain: Interceptor.Chain ->
    val request = chain.request()
    val url = request.url.toString()
    val baseUrl = url.indexOf(BASE_URL)
    // 如果 url 中包含 BASE_URL，则根据是否包含 @CaiYunBaseUrl 或 @SobBaseUrl 注解来设置请求的 BaseUrl，否则使用原来的 url。
    val newUrl = baseUrl.takeUnless { it == -1 }?.let { request.handleUrlPrefix().plus(url.substringAfter(BASE_URL)) } ?: url
    val newRequest = request.newBuilder()
        .url(newUrl)
        .build()
    chain.proceed(newRequest)
}

private fun Request.handleUrlPrefix(): String {
    getAnnotations().forEach { annotation ->
        annotation.getCustomAnnotation<BaseUrl>().takeUnless { it == null }?.let { return it.value }
    }
    return BASE_URL
}

private fun Request.getAnnotations(): Array<Annotation> =
    tag(Invocation::class.java)?.method()?.annotations ?: arrayOf()

private inline fun <reified T : Annotation> Request.getCustomAnnotation(): T? =
    tag(Invocation::class.java)?.method()?.getAnnotation(T::class.java)
