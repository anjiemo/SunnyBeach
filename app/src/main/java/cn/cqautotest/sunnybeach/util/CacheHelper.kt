package cn.cqautotest.sunnybeach.util

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import timber.log.Timber
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/26
 * desc   : 数据缓存帮助类，可以用于缓存网络请求的数据（一般只缓存 GET 请求返回的数据）。
 * 不建议缓存时效性较要求高的数据。
 */
object CacheHelper {

    private val mCacheImpl: DataCache = DefaultCacheImpl()

    fun checkExpired(cacheKey: String, expireDuration: Long = TimeUnit.MINUTES.toMillis(15)): Boolean = mCacheImpl.checkExpired(cacheKey, expireDuration)

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
    fun <T> saveToCache(cacheKey: String, data: T) {
        data ?: return
        when (data) {
            is Map<*, *> -> {
                if (data.isNotEmpty()) {
                    internalCache(cacheKey, data)
                }
            }

            is Collection<*> -> {
                if (data.isNotEmpty()) {
                    internalCache(cacheKey, data)
                }
            }

            is Array<*> -> {
                if (data.isNotEmpty()) {
                    internalCache(cacheKey, data)
                }
            }

            else -> {
                internalCache(cacheKey, data)
            }
        }
    }

    /**
     * 真正执行缓存数据的逻辑
     */
    private fun <T> internalCache(cacheKey: String, data: T) {
        try {
            mCacheImpl.onCache(cacheKey, data)
            Timber.d("internalCache：===> saveToCache cacheKey is $cacheKey")
        } catch (t: Throwable) {
            Timber.e(t, "are you ok? cache failed.")
        }
    }

    class DefaultCacheImpl : DataCache {

        /**
         * 默认为 15 分钟失效，失效后需要重新获取数据并进行缓存
         */
        override fun checkExpired(cacheKey: String, expireDuration: Long): Boolean {
            val cacheTime = mMMKV.decodeLong(getCacheTimeKey(cacheKey), 0L)
            return System.currentTimeMillis() - cacheTime > expireDuration
        }

        override fun <T> onCache(cacheKey: String, data: T) {
            val jsonData = mGson.toJson(data)
            mMMKV.encode(getCacheTimeKey(cacheKey), System.currentTimeMillis())
            mMMKV.encode(getCacheDataKey(cacheKey), jsonData)
        }

        override fun <T> getFromCache(cacheKey: String, type: Type): T? {
            val jsonData = mMMKV.decodeString(getCacheDataKey(cacheKey), null)
            return mGson.fromJson(jsonData, type) as T?
        }

        companion object {

            private const val MMKV_CACHE_KEY = "MMKV_CACHE_KEY"
            private val mGson = GsonUtils.getGson()
            private val mMMKV by lazy { MMKV.mmkvWithID(MMKV_CACHE_KEY, MMKV.MULTI_PROCESS_MODE) }
        }
    }

    interface DataCache {

        /**
         * 检查缓存是否过期
         */
        fun checkExpired(cacheKey: String, expireDuration: Long): Boolean

        /**
         * 获取缓存时间的 Key
         */
        fun getCacheTimeKey(cacheKey: String) = MMKV_CACHE_TIME_KEY + cacheKey

        /**
         * 获取缓存数据的 Key
         */
        fun getCacheDataKey(cacheKey: String) = MMKV_CACHE_DATA_KEY + cacheKey

        /**
         * 缓存数据
         */
        fun <T> onCache(cacheKey: String, data: T)

        /**
         * 从缓存获取数据
         */
        fun <T> getFromCache(cacheKey: String, type: Type): T?

        companion object {

            private const val MMKV_CACHE_TIME_KEY = "MMKV_CACHE_TIME_KEY_"
            private const val MMKV_CACHE_DATA_KEY = "MMKV_CACHE_DATA_KEY_"
        }
    }
}