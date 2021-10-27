package cn.cqautotest.sunnybeach.http

import cn.cqautotest.sunnybeach.manager.CookieManager
import cn.cqautotest.sunnybeach.util.BASE_URL
import cn.cqautotest.sunnybeach.util.logByDebug
import cn.cqautotest.sunnybeach.util.unicodeToString
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/02
 * desc   : 网络请求服务创建者
 */
object ServiceCreator {

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor {
            logByDebug(msg = "===> result：${it.unicodeToString()}")
        }.also {
            it.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
    private val loginInterceptor by lazy { LoginInterceptor() }

    private var cookieManager = CookieManager.get()

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
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}