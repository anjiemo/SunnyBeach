package cn.funkt.interceptor

import cn.funkt.annotation.BaseUrl
import cn.funkt.annotation.Ignore
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import retrofit2.Retrofit
import java.lang.reflect.Method

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/13
 * desc   : BaseUrl 拦截器，能够根据接口方法或类上的 @xxxBaseUrl、@Ignore 注解智能的替换请求的 baseUrl。
 */
class BaseUrlInterceptor(private val retrofitProvider: () -> Retrofit) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // 获取到当前请求的 request 对象
        with(chain.request()) {
            // 如果方法上和类上的 BaseUrl 注解都拿不到，则不处理
            val baseUrlAnnotation = getBaseUrlAnnotationUrl() ?: return chain.proceed(this)
            // 能走到这里，则代表我们已经获取到了 BaseUrl 注解，开始替换 BaseUrl 的操作
            // 获取新的完整 url 或原来的 url
            val newUrl = getNewUrlOrNothing(baseUrlAnnotation)
            // 使用新的 url 地址构建 request 对象
            val newRequest = newBuilder()
                .url(newUrl)
                .build()
            // 使用新的 request 对象执行请求
            return chain.proceed(newRequest)
        }
    }

    /**
     * 获取新的完整请求的 url
     */
    private fun Request.getNewUrlOrNothing(baseUrlAnnotation: BaseUrl): String {
        // 获取 retrofit 实例
        val retrofit = retrofitProvider.invoke()
        // 我们先拿到当前请求的 url 地址（该 url 地址是全路径地址）
        val oldUrl = url.toString()
        // 获取 retrofit 实例 HttpUrl 类型的 baseUrl
        val httpUrl = retrofit.baseUrl()
        // 拿到 retrofit 实例的 baseUrl
        val baseUrl = httpUrl.toString()
        // 如果 oldUrl 中包含了 retrofit 的 baseUrl 则代表需要被替换
        return takeUnless { oldUrl.indexOf(baseUrl) == -1 }?.let {
            // 执行替换操作，拿到不包含 baseUrl 的部分路径
            val suffixUrl = oldUrl.replaceFirst(baseUrl, "")
            // 拼接替换 baseUrl 后的完整 url
            baseUrlAnnotation.value + suffixUrl
        } ?: oldUrl
    }

    /**
     * 通过 Request 对象获取 baseUrl 注解，如果获取不到则返回 `null`
     */
    private fun Request.getBaseUrlAnnotationUrl(): BaseUrl? {
        // 获取到请求的调用标记对象，如果获取不到则不处理该请求
        val invocation = tag(Invocation::class.java) ?: return null
        // 获取到当前请求的 Method 对象
        val method = invocation.method()
        // 如果该请求包含 Ignore 注解，则不做处理
        method.getIgnoreAnnotationOrNull()?.let { return null }
        // 拿到方法上的 BaseUrl 注解，如果拿不到，则拿到类上面的 BaseUrl 注解
        return method.getBaseUrlAnnotationOrNull() ?: method.declaringClass.getBaseUrlAnnotationOrNull()
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
