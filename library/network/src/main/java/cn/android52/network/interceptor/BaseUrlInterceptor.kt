package cn.android52.network.interceptor

import cn.android52.network.annotation.BaseUrl
import cn.android52.network.annotation.Ignore
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.lang.reflect.Method

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/13
 * desc   : BaseUrl 拦截器，参数为 baseUrl 的 Set 集合，此集合的存在可以使当前拦截器给多个 Retrofit 实例共用而不需要创建多个 BaseUrlInterceptor 实例
 */
class BaseUrlInterceptor(private val baseUrlSet: Set<String>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // 获取到当前请求的 request 对象
        with(chain.request()) {
            // 获取到请求的调用标记对象，如果获取不到则不处理该请求
            val invocation = tag(Invocation::class.java) ?: return chain.proceed(this)
            // 获取到当前请求的 Method 对象
            val method = invocation.method()
            // 如果该请求包含 Ignore 注解，则不做处理
            method.getIgnoreAnnotationOrNull()?.let { return chain.proceed(this) }
            // 拿到方法上的 BaseUrl 注解，如果拿不到，则拿到类上面的 BaseUrl 注解
            val baseUrlAnnotation = method.getBaseUrlAnnotationOrNull() ?: method.declaringClass.getBaseUrlAnnotationOrNull()
            // 如果方法上和类上的 BaseUrl 注解都拿不到，则不处理
            baseUrlAnnotation ?: return chain.proceed(this)
            // 能走到这里，则代表我们已经获取到了 BaseUrl 注解，开始替换 BaseUrl 的操作
            // 我们先拿到当前请求的 url 地址（该 url 地址是全路径地址）
            val oldUrl = url.toString()
            // 如果 url 以 BaseUrl 开始，则返回去除 BaseUrl 后的后缀 url，否则返回 null
            val suffixUrl = baseUrlSet.firstOrNull { oldUrl.startsWith(it) }?.let { oldUrl.replaceFirst(it, "") }
            // 后缀 url 不为 null，则返回替换后的 url，否则返回原来的 url
            val newUrl = if (suffixUrl != null) baseUrlAnnotation.value + suffixUrl else oldUrl
            // 使用新的 url 地址构建 request 对象
            val newRequest = newBuilder()
                .url(newUrl)
                .build()
            // 使用新的 request 对象执行请求
            return chain.proceed(newRequest)
        }
    }

    /**
     * 获取到 Ignore 注解，如果获取不到则返回 `null`
     */
    private fun Method.getIgnoreAnnotationOrNull(): Ignore? = getAnnotation(Ignore::class.java)

    /**
     * 获取到 BaseUrl 注解，如果获取不到则返回 `null`
     */
    private fun Class<*>.getBaseUrlAnnotationOrNull(): BaseUrl? = annotations.firstOrNull()

    /**
     * 获取到 BaseUrl 注解，如果获取不到则返回 `null`
     */
    private fun Method.getBaseUrlAnnotationOrNull(): BaseUrl? = annotations.firstOrNull()

    /**
     * 通过遍历获取到第一个指定类型的注解，如果获取不到则返回 `null`
     */
    private inline fun <reified T : Annotation> Array<Annotation>.firstOrNull(): T? {
        forEach { annotation ->
            annotation.getCustomAnnotationOrNull<T>().takeUnless { it == null }?.let { return it }
        }
        return null
    }

    /**
     * 获取到自定义注解，如果获取不到则返回 `null`
     */
    private inline fun <reified T : Annotation> Annotation.getCustomAnnotationOrNull(): T? =
        annotationClass.java.getAnnotation(T::class.java)
}
