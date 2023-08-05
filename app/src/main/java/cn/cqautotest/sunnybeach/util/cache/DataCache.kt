package cn.cqautotest.sunnybeach.util.cache

import java.lang.reflect.Type

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/06/14
 * desc   : 数据缓存接口。若要实现缓存逻辑，请实现该接口。
 */
interface DataCache {

    /**
     * 检查缓存是否过期
     */
    fun checkExpired(cacheKey: String, expireDuration: Long): Boolean

    /**
     * 获取缓存时间的 Key
     */
    fun getCacheTimeKey(cacheKey: String) = DATA_CACHE_TIME_KEY + cacheKey

    /**
     * 获取缓存数据的 Key
     */
    fun getCacheDataKey(cacheKey: String) = DATA_CACHE_DATA_KEY + cacheKey

    /**
     * 缓存数据
     */
    fun onCache(cacheKey: String, data: Any?)

    /**
     * 从缓存获取数据
     */
    fun <T> getFromCache(cacheKey: String, type: Type): T?

    companion object {

        private const val DATA_CACHE_TIME_KEY = "DATA_CACHE_TIME_KEY_"
        private const val DATA_CACHE_DATA_KEY = "DATA_CACHE_DATA_KEY_"
    }
}