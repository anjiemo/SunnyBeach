package cn.cqautotest.sunnybeach.http

import cn.cqautotest.sunnybeach.manager.LocalCookieManager
import cn.cqautotest.sunnybeach.util.BASE_URL
import cn.cqautotest.sunnybeach.util.CAI_YUN_BASE_URL
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import cn.cqautotest.sunnybeach.util.unicodeToString
import com.hjq.gson.factory.GsonFactory
import com.huawei.hms.scankit.p.T
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
        // TODO: 2022/04/18 根据 Api 类型使用不同的 Retrofit 实例创建请求 Api 实例
        // val api = when (T::class) {
        //     is UserApi -> sobRetrofit.create(T::class.java)
        //     is ISobApi -> sobRetrofit.create(T::class.java)
        //     is AppApi -> otherRetrofit.create(T::class.java)
        //     is PhotoApi -> otherRetrofit.create(T::class.java)
        //     else -> sobRetrofit.create(T::class.java)
        // }
        // Timber.d("create：===> api is ${T::class.java} ---> ${T::class.java is ISobApi} ${T::class.java is UserApi}")
        return sobRetrofit.create(T::class.java)
    }
}