package cn.android52.network.interceptor

import cn.android52.network.annotation.BaseUrl
import cn.android52.network.ktx.getCustomAnnotationOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/13
 * desc   : BaseUrl 拦截器，根据请求接口的 @xxxBaseUrl 注解来设置请求的 BaseUrl，
 * 需要拦截的接口必须加 @BaseUrl 注解，且 需要将可替换的 BaseUrl 都放到 baseUrlSet 的集合中。
 */
class BaseUrlInterceptor(private val baseUrlSet: Set<String>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        with(chain.request()) {
            val oldUrl = url.toString()

            // 获取 request 中包含 @BaseUrl 注解的注解或返回 null。
            val baseUrlAnnotation = getBaseUrlAnnotationOrNull()

            // Log.d(TAG, "intercept: ===> baseUrlAnnotation is baseUrlAnnotation")

            // 如果 url 中包含需要替换的 BaseUrl，则返回去除 BaseUrl 后的后缀 url，否则返回 null。
            val suffixUrl = baseUrlSet.firstOrNull { oldUrl.indexOf(it) != -1 }?.let { oldUrl.replaceFirst(it, "") }

            // Log.d(TAG, "intercept: ===> oldUrl is $oldUrl")

            // 如果该 request 包含 @xxxBaseUrl 注解的注解，且后缀 url 不为 null，则返回替换后的 url，否则返回原来的 url。
            val newUrl = if (baseUrlAnnotation != null && suffixUrl != null) baseUrlAnnotation.value + suffixUrl else oldUrl

            // Log.d(TAG, "intercept: ===> newUrl is $newUrl")

            val newRequest = newBuilder()
                .url(newUrl)
                .build()

            return chain.proceed(newRequest)
        }
    }

    private fun Request.getBaseUrlAnnotationOrNull(): BaseUrl? {
        getAnnotations().forEach { annotation ->
            annotation.getCustomAnnotationOrNull<BaseUrl>().takeUnless { it == null }?.let { return it }
        }
        return null
    }

    private fun Request.getAnnotations(): Array<Annotation> =
        tag(Invocation::class.java)?.method()?.annotations ?: arrayOf()

    private inline fun <reified T : Annotation> Request.getCustomAnnotation(): T? =
        tag(Invocation::class.java)?.method()?.getAnnotation(T::class.java)

    companion object {

        private const val TAG = "BaseUrlInterceptor"
    }
}
