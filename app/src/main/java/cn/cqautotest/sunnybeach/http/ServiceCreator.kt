package cn.cqautotest.sunnybeach.http

import cn.cqautotest.sunnybeach.manager.LocalCookieManager
import cn.cqautotest.sunnybeach.util.BASE_URL
import cn.cqautotest.sunnybeach.util.unicodeToString
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
    private val loginInterceptor by lazy { AccountInterceptor() }

    private var cookieManager = LocalCookieManager.get()

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(loginInterceptor)
            .cookieJar(cookieManager)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
            .client(client)
            .build()
    }

    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}