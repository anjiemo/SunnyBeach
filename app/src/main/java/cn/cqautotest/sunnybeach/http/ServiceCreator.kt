package cn.cqautotest.sunnybeach.http

import cn.cqautotest.sunnybeach.http.annotation.CaiYunClient
import cn.cqautotest.sunnybeach.http.annotation.SobClient
import cn.cqautotest.sunnybeach.http.interceptor.accountInterceptor
import cn.cqautotest.sunnybeach.http.interceptor.baseUrlInterceptor
import cn.cqautotest.sunnybeach.http.interceptor.loggingInterceptor
import cn.cqautotest.sunnybeach.manager.LocalCookieManager
import cn.cqautotest.sunnybeach.util.BASE_URL
import cn.cqautotest.sunnybeach.util.CAI_YUN_BASE_URL
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import com.hjq.gson.factory.GsonFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/02
 * desc   : 网络请求服务创建者
 */
object ServiceCreator {

    val clientCache = ConcurrentHashMap<Class<*>, Retrofit>(3)

    val sobRetrofit: Retrofit by lazy { createRetrofit { baseUrl(SUNNY_BEACH_API_BASE_URL) } }

    val caiYunRetrofit: Retrofit by lazy { createRetrofit { baseUrl(CAI_YUN_BASE_URL) } }

    val otherRetrofit: Retrofit by lazy { createRetrofit { baseUrl(BASE_URL) } }

    private val cookieManager = LocalCookieManager.get()

    val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(baseUrlInterceptor)
            .addInterceptor(accountInterceptor)
            .addInterceptor(loggingInterceptor)
            .cookieJar(cookieManager)
            .build()
    }

    private fun createRetrofit(block: Retrofit.Builder.() -> Retrofit.Builder) = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
        .client(client)
        .run(block)
        .build()

    inline fun <reified T> create(): T {
        val clazz = T::class.java
        return getOrCreate(clazz)
    }

    inline fun <reified T> getOrCreate(clazz: Class<T>): T {
        // Get it from the cache first, and return directly if the cache hits.
        clientCache[clazz]?.let { return it.create(clazz) }
        // We didn't find it in the cache, return the retrofit instance according to the annotation information.
        val retrofit = when {
            clazz containsAnnotation SobClient::class.java -> sobRetrofit
            clazz containsAnnotation CaiYunClient::class.java -> caiYunRetrofit
            else -> otherRetrofit
        }
        // Cached for the convenience of direct use next time.
        clientCache[clazz] = retrofit
        return retrofit.create(clazz)
    }

    /**
     * Check if the class contains that annotation.
     */
    infix fun <A : Class<*>, B : Annotation> A.containsAnnotation(that: Class<B>): Boolean = getAnnotation(that) != null
}