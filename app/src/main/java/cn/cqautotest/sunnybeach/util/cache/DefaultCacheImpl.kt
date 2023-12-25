package cn.cqautotest.sunnybeach.util.cache

import com.blankj.utilcode.util.GsonUtils
import com.tencent.mmkv.MMKV
import java.lang.reflect.Type

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/06/14
 * desc   : 默认的数据缓存实现
 */
class DefaultCacheImpl : DataCache {

    override fun checkExpired(cacheKey: String, expireDuration: Long): Boolean {
        val cacheTime = mMMKV.decodeLong(getCacheTimeKey(cacheKey), 0L)
        return System.currentTimeMillis() - cacheTime > expireDuration
    }

    override fun onCache(cacheKey: String, data: Any?) {
        val jsonData = mGson.toJson(data)
        mMMKV.encode(getCacheTimeKey(cacheKey), System.currentTimeMillis())
        mMMKV.encode(getCacheDataKey(cacheKey), jsonData)
    }

    override fun <T> getFromCache(cacheKey: String, type: Type): T? {
        val jsonData = mMMKV.decodeString(getCacheDataKey(cacheKey), null)
        return mGson.fromJson(jsonData, type) as T?
    }

    companion object {

        private const val MMKV_CACHE_KEY = "MMKV_CACHE_DATA"
        private val mGson = GsonUtils.getGson()
        private val mMMKV by lazy { MMKV.mmkvWithID(MMKV_CACHE_KEY, MMKV.MULTI_PROCESS_MODE) }
    }
}