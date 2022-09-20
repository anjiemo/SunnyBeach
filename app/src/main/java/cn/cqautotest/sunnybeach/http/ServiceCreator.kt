package cn.cqautotest.sunnybeach.http

import cn.android52.network.interceptor.BaseUrlInterceptor
import cn.cqautotest.sunnybeach.http.interceptor.accountInterceptor
import cn.cqautotest.sunnybeach.http.interceptor.loggingInterceptor
import cn.cqautotest.sunnybeach.manager.LocalCookieManager
import cn.cqautotest.sunnybeach.util.BASE_URL
import com.hjq.gson.factory.GsonFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/02
 * desc   : 网络请求服务创建者
 */
object ServiceCreator {

    val retrofit: Retrofit by lazy { createRetrofit { baseUrl(BASE_URL) } }

    private val cookieManager = LocalCookieManager.get()

    val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(BaseUrlInterceptor { retrofit })
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

    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}