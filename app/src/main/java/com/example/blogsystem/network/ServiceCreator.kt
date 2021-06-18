package com.example.blogsystem.network

import com.example.blogsystem.utils.BASE_URL
import com.example.blogsystem.utils.CookieManager
import com.example.blogsystem.utils.logByDebug
import com.example.blogsystem.utils.unicodeToString
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private val interceptor by lazy {
        HttpLoggingInterceptor {
            logByDebug(msg = "===> result：${it.unicodeToString()}")
        }.also {
            it.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
    private val cookieManager by lazy { CookieManager() }
    val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
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