package cn.android52.network.interceptor

import cn.android52.network.annotation.BaseUrl
import cn.android52.network.annotation.Ignore
import cn.android52.network.ktx.getCustomAnnotationOrNull
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.lang.reflect.Method

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/13
 * desc   : BaseUrl 拦截器
 */
class BaseUrlInterceptor(private val baseUrlSet: Set<String>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        with(chain.request()) {
            val invocation = tag(Invocation::class.java) ?: return chain.proceed(this)
            val method = invocation.method()
            // 如果该请求包含 Ignore 注解，则不做处理
            method.getIgnoreAnnotationOrNull()?.let { return chain.proceed(this) }
            // 拿到方法上的 BaseUrl 注解，如果拿不到，则拿到类上面的 BaseUrl 注解
            val baseUrlAnnotation = method.getBaseUrlAnnotationOrNull() ?: method.declaringClass.getBaseUrlAnnotationOrNull()
            // 如果方法上和类上的 BaseUrl 注解都拿不到，则不处理
            baseUrlAnnotation ?: return chain.proceed(this)
            val oldUrl = url.toString()
            // Log.d(TAG, "intercept: ===> baseUrlAnnotation is baseUrlAnnotation")
            // 如果 url 中包含需要替换的 BaseUrl，则返回去除 BaseUrl 后的后缀 url，否则返回 null。
            val suffixUrl = baseUrlSet.firstOrNull { oldUrl.indexOf(it) != -1 }?.let { oldUrl.replaceFirst(it, "") }
            // Log.d(TAG, "intercept: ===> oldUrl is $oldUrl")
            // 后缀 url 不为 null，则返回替换后的 url，否则返回原来的 url。
            val newUrl = if (suffixUrl != null) baseUrlAnnotation.value + suffixUrl else oldUrl
            // Log.d(TAG, "intercept: ===> newUrl is $newUrl")
            val newRequest = newBuilder()
                .url(newUrl)
                .build()
            return chain.proceed(newRequest)
        }
    }

    private fun Class<*>.getBaseUrlAnnotationOrNull(): BaseUrl? = annotations.firstOrNull()

    private fun Method.getBaseUrlAnnotationOrNull(): BaseUrl? = annotations.firstOrNull()

    private fun Method.getIgnoreAnnotationOrNull(): Ignore? = getAnnotation(Ignore::class.java)

    private inline fun <reified T : Annotation> Array<Annotation>.firstOrNull(): T? {
        forEach { annotation ->
            annotation.getCustomAnnotationOrNull<T>().takeUnless { it == null }?.let { return it }
        }
        return null
    }

    companion object {

        private const val TAG = "BaseUrlInterceptor"
    }
}
