package cn.cqautotest.sunnybeach.util

import cn.cqautotest.sunnybeach.util.cache.DataCache
import cn.cqautotest.sunnybeach.util.cache.DefaultCacheImpl
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/26
 * desc   : 数据缓存帮助类，可以用于缓存网络请求的数据（一般只缓存 GET 请求返回的数据）。
 * 不建议缓存时效性较要求高的数据。
 * 不支持缓存接口对象 或 元素为接口类型的数组和集合，否则会在反序列化的时候失败，从而获取到无效数据，此问题由 Gson 产生。如：
 * interface Data
 * Collection<Data>
 * Array<Data>
 */
object CacheHelper {

    private val mCacheImpl: DataCache = DefaultCacheImpl()

    /**
     * 默认为 15 分钟失效，失效后需要重新获取数据并进行缓存
     */
    fun checkExpired(cacheKey: String, expireDuration: Long = TimeUnit.MINUTES.toMillis(15)): Boolean =
        mCacheImpl.checkExpired(cacheKey, expireDuration)

    inline fun <reified T> getFromCache(cacheKey: String): T? = getFromCache(cacheKey, object : TypeToken<T>() {}.type)

    /**
     * 从本地缓存获取数据
     */
    fun <T> getFromCache(cacheKey: String, type: Type): T? {
        return try {
            mCacheImpl.getFromCache(cacheKey, type)
        } catch (t: Throwable) {
            Timber.e(t, "are you ok? get cache failed.")
            null
        }
    }

    /**
     * 保存数据到本地缓存
     */
    fun saveToCache(cacheKey: String, data: Any?) {
        if (!isValidateData(data)) return
        mCacheImpl.onCache(cacheKey, data)
    }

    /**
     * 校验数据
     */
    private fun isValidateData(data: Any?) = when (data) {
        null -> false
        is Map<*, *> -> data.isNotEmpty()
        is Collection<*> -> data.isNotEmpty()
        is Array<*> -> data.isNotEmpty()
        else -> true
    }
}