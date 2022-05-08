package cn.cqautotest.sunnybeach.http

import cn.cqautotest.sunnybeach.http.api.sob.ISobApi
import cn.cqautotest.sunnybeach.http.api.weather.ICaiYunApi
import cn.cqautotest.sunnybeach.ktx.unicodeToString
import cn.cqautotest.sunnybeach.manager.LocalCookieManager
import cn.cqautotest.sunnybeach.util.BASE_URL
import cn.cqautotest.sunnybeach.util.CAI_YUN_BASE_URL
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import com.hjq.gson.factory.GsonFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/02
 * desc   : 网络请求服务创建者
 */
object ServiceCreator {

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor {
            Timber.d("===> result：${it.unicodeToString()}")
        }.also {
            it.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
    private val accountInterceptor by lazy { AccountInterceptor() }
    private val cookieManager = LocalCookieManager.get()

    val client by lazy {
        OkHttpClient.Builder()
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

    val sobRetrofit: Retrofit = createRetrofit { baseUrl(SUNNY_BEACH_API_BASE_URL) }

    val caiYunRetrofit: Retrofit = createRetrofit { baseUrl(CAI_YUN_BASE_URL) }

    val otherRetrofit: Retrofit = createRetrofit { baseUrl(BASE_URL) }

    inline fun <reified T> create(): T {
        // Create request Api instance with different Retrofit instance according to Api type, generic type T cannot be null.
        val clazz = T::class.java
        val api = when {
            ISobApi::class.java.isAssignableFrom(clazz) -> sobRetrofit.create(clazz)
            ICaiYunApi::class.java.isAssignableFrom(clazz) -> caiYunRetrofit.create(clazz)
            else -> otherRetrofit.create(clazz)
        }
        return api
    }
}